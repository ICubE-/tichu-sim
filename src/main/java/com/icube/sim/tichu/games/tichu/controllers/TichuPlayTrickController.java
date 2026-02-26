package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.TrickSend;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTrickException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class TichuPlayTrickController {
    private final TichuService tichuService;

    @MessageMapping("/rooms/{roomId}/game/tichu/play-trick")
    public void playTrick(
            @DestinationVariable("roomId") String roomId,
            @Payload TrickSend trickSend,
            Principal principal
    ) {
        tichuService.playTrick(roomId, trickSend, principal);
    }

    @MessageExceptionHandler(InvalidTrickException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTrick() {
        return new ErrorMessage("Invalid trick.");
    }
}
