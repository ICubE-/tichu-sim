package com.icube.sim.tichu.rooms;

import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
public class RoomInboundChannelInterceptor implements ChannelInterceptor {
    private final PathPattern subscribePattern;
    private final PathPattern sendPattern;
    private final RoomRepository roomRepository;

    public RoomInboundChannelInterceptor(RoomRepository roomRepository) {
        PathPatternParser pathPatternParser = new PathPatternParser();
        this.subscribePattern = pathPatternParser.parse("/api/ws/topic/rooms/{roomId}/**");
        this.sendPattern = pathPatternParser.parse("/api/ws/app/rooms/{roomId}/**");
        this.roomRepository = roomRepository;
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        var user = accessor.getUser();
        assert user != null;
        var userId = Long.valueOf(user.getName());

        var destination = accessor.getDestination();
        if (destination == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var sessionAttributes = accessor.getSessionAttributes();
            assert sessionAttributes != null;
            sessionAttributes.put("userId", userId);
        }
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            checkDestination(destination, subscribePattern, "Invalid subscribe path.", userId);
        }
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            checkDestination(destination, sendPattern, "Invalid send path.", userId);
        }

        return message;
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
