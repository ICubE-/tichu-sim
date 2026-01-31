package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.auth.jwt.JwtService;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collections;

@Component
public class RoomInboundChannelInterceptor implements ChannelInterceptor {
    private final PathPattern subscribePattern;
    private final PathPattern sendPattern;
    private final RoomRepository roomRepository;
    private final JwtService jwtService;

    public RoomInboundChannelInterceptor(RoomRepository roomRepository, JwtService jwtService) {
        PathPatternParser pathPatternParser = new PathPatternParser();
        this.subscribePattern = pathPatternParser.parse("/topic/rooms/{roomId}/**");
        this.sendPattern = pathPatternParser.parse("/app/rooms/{roomId}/**");
        this.roomRepository = roomRepository;
        this.jwtService = jwtService;
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        var sessionAttributes = accessor.getSessionAttributes();
        assert sessionAttributes != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessageDeliveryException("Access denied.");
            }

            var token = authHeader.replace("Bearer ", "");
            var jwt = jwtService.parse(token).orElse(null);
            if (jwt == null || jwt.isExpired()) {
                throw new MessageDeliveryException("Access denied.");
            }

            sessionAttributes.put("userId", jwt.getUserId());
        }

        var userId = (Long) sessionAttributes.get("userId");
        if (userId != null) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.emptyList()
            );
            accessor.setUser(authentication);
        }

        var destination = accessor.getDestination();
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            checkDestination(destination, subscribePattern, "Invalid subscribe path.", userId);
        }
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            checkDestination(destination, sendPattern, "Invalid send path.", userId);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private void checkDestination(
            String destination,
            PathPattern validPattern,
            String matchFailDescription,
            Long userId
    ) {
        var path = PathContainer.parsePath(destination);
        var matchInfo = validPattern.matchAndExtract(path);
        if (matchInfo == null) {
            throw new MessageDeliveryException(matchFailDescription);
        }

        var roomId = matchInfo.getUriVariables().get("roomId");
        var room = roomRepository.findById(roomId).orElseThrow(() -> new MessageDeliveryException("Room not found."));
        if (!room.containsMember(userId)) {
            throw new MessageDeliveryException("User not in the room.");
        }
    }
}
