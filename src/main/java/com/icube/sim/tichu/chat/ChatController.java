package com.icube.sim.tichu.chat;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {
    @MessageMapping("/rooms/{roomId}/chat")
    @SendTo("/api/ws/topic/rooms/{roomId}/chat")
    public ChatMessage chat(
            @DestinationVariable("roomId") String roomId,
            @Payload ChatSend chatMessage,
            Principal principal
    ) {
        var userId = Long.valueOf(principal.getName());
        return new ChatMessage(userId, chatMessage.getMessage());
    }
}
