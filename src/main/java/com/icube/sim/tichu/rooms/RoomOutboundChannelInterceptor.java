package com.icube.sim.tichu.rooms;

import org.jspecify.annotations.NonNull;
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

import java.security.Principal;

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
            var roomId = tryParseAndExtractRoomId(destination, messagePattern);
            if (roomId != null && !isRoomMember(roomId, accessor.getUser())) {
                return null;
            }
        }

        return message;
    }

    private String tryParseAndExtractRoomId(String destination, PathPattern pattern) {
        var path = PathContainer.parsePath(destination);
        var matchInfo = pattern.matchAndExtract(path);
        if (matchInfo == null) {
            return null;
        }
        return matchInfo.getUriVariables().get("roomId");
    }

    private boolean isRoomMember(
            String roomId,
            @Nullable Principal principal
    ) {
        assert principal != null;
        var userId = Long.valueOf(principal.getName());
        var room = roomRepository.findById(roomId).orElse(null);
        return room != null && room.containsMember(userId);
    }
}
