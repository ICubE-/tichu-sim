package com.icube.sim.tichu.common.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

public interface DestinationCheckerInterceptor {
    String HEADER_NAME = "isDestinationChecked";

    default Message<?> destinationChecked(Message<?> message) {
        return destinationChecked(message, true);
    }

    default Message<?> destinationChecked(Message<?> message, boolean isChecked) {
        var accessor = StompHeaderAccessor.wrap(message);
        accessor.setHeader(HEADER_NAME, isChecked);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
