package com.icube.sim.tichu.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@AllArgsConstructor
@Component
public class JwtAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        var sessionAttributes = accessor.getSessionAttributes();
        assert sessionAttributes != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessageDeliveryException(message, "Access denied.");
            }

            var token = authHeader.replace("Bearer ", "");
            var jwt = jwtService.parse(token).orElse(null);
            if (jwt == null || jwt.isExpired()) {
                throw new MessageDeliveryException(message, "Access denied.");
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

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
