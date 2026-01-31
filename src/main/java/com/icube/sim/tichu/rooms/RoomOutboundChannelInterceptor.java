package com.icube.sim.tichu.rooms;

import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
public class RoomOutboundChannelInterceptor implements ChannelInterceptor {
    private final PathPattern messagePattern;
    private final RoomRepository roomRepository;

    public RoomOutboundChannelInterceptor(RoomRepository roomRepository) {
        PathPatternParser pathPatternParser = new PathPatternParser();
        this.messagePattern = pathPatternParser.parse("/topic/rooms/{roomId}/**");
        this.roomRepository = roomRepository;
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        var destination = accessor.getDestination();
        if (destination == null) {
            return message;
        }

        if (StompCommand.MESSAGE.equals(accessor.getCommand())) {
            var sessionAttributes = accessor.getSessionAttributes();
            assert sessionAttributes != null;
            var userId = (Long) sessionAttributes.get("userId");

            if (!isDestinationValid(destination, messagePattern, userId)) {
                return null;
            }
        }

        return message;
    }

    private boolean isDestinationValid(
            String destination,
            PathPattern validPattern,
            Long userId
    ) {
        var path = PathContainer.parsePath(destination);
        var matchInfo = validPattern.matchAndExtract(path);
        if (matchInfo == null) {
            return false;
        }

        var roomId = matchInfo.getUriVariables().get("roomId");
        var room = roomRepository.findById(roomId).orElse(null);
        return room != null && room.containsMember(userId);
    }
}
