package com.icube.sim.tichu.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessage {
    private Long userId;
    private String message;
}
