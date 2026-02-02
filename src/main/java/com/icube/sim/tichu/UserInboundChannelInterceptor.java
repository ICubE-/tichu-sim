package com.icube.sim.tichu;

import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.PathContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class UserInboundChannelInterceptor implements ChannelInterceptor, DestinationCheckerInterceptor {
    private final PathPattern pattern;

    public UserInboundChannelInterceptor() {
        PathPatternParser pathPatternParser = new PathPatternParser();
        this.pattern = pathPatternParser.parse("/user/{userId}/**");
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        var destination = accessor.getDestination();
        if (destination == null) {
            return message;
        }

        var userId = tryParseAndExtractUserId(destination);
        if (userId == null) {
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            var user = accessor.getUser();
            assert user != null;
            if (!user.getName().equals(userId)) {
                throw new MessageDeliveryException(message, "Access denied.");
            }
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            throw new MessageDeliveryException(message, "Access denied.");
        }

        return destinationChecked(message);
    }

    private String tryParseAndExtractUserId(String destination) {
        var path = PathContainer.parsePath(destination);
        var matchInfo = pattern.matchAndExtract(path);
        if (matchInfo == null) {
            return null;
        }
        return matchInfo.getUriVariables().get("userId");
    }
}
