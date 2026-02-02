package com.icube.sim.tichu.common.websocket;

import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

public class DestinationGuardInterceptor implements ChannelInterceptor {
    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        var isDestinationChecked = accessor.getHeader(DestinationCheckerInterceptor.HEADER_NAME);
        assert isDestinationChecked instanceof Boolean;
        if (!(boolean) isDestinationChecked && accessor.getDestination() != null) {
            throw new MessageDeliveryException(message, "Invalid destination.");
        }

        accessor.removeHeader(DestinationCheckerInterceptor.HEADER_NAME);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
