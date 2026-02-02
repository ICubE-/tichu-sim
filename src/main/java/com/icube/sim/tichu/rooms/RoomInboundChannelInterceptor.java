package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.DestinationCheckerInterceptor;
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

import java.security.Principal;

@Component
public class RoomInboundChannelInterceptor implements ChannelInterceptor, DestinationCheckerInterceptor {
    private final PathPattern subscribePattern;
    private final PathPattern sendPattern;
    private final RoomRepository roomRepository;

    public RoomInboundChannelInterceptor(RoomRepository roomRepository) {
        PathPatternParser pathPatternParser = new PathPatternParser();
        this.subscribePattern = pathPatternParser.parse("/topic/rooms/{roomId}/**");
        this.sendPattern = pathPatternParser.parse("/app/rooms/{roomId}/**");
        this.roomRepository = roomRepository;
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        var destination = accessor.getDestination();
        if (destination == null) {
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            var roomId = tryParseAndExtractRoomId(destination, subscribePattern);
            if (roomId != null) {
                checkRoomMembers(roomId, accessor.getUser());
                return destinationChecked(message);
            }
        }
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            var roomId = tryParseAndExtractRoomId(destination, sendPattern);
            if (roomId != null) {
                checkRoomMembers(roomId, accessor.getUser());
                return destinationChecked(message);
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

    private void checkRoomMembers(
            String roomId,
            @Nullable Principal principal
    ) {
        var room = roomRepository.findById(roomId).orElseThrow(() -> new MessageDeliveryException("Room not found."));

        assert principal != null;
        var userId = Long.valueOf(principal.getName());
        if (!room.containsMember(userId)) {
            throw new MessageDeliveryException("User not in the room.");
        }
    }
}
