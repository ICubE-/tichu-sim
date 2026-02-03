package com.icube.sim.tichu.common.websocket;

import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class DestinationCheckInitializeInterceptor implements ChannelInterceptor, DestinationCheckerInterceptor {
    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        return destinationChecked(message, false);
    }
}
