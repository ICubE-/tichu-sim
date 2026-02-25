package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.BombSend;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidBombException;
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
public class TichuPlayBombController {
    private final TichuService tichuService;

    @MessageMapping("/rooms/{roomId}/game/tichu/play-bomb")
    public void playBomb(
            @DestinationVariable("roomId") String roomId,
            @Payload BombSend bombSend,
            Principal principal
    ) {
        tichuService.playBomb(roomId, bombSend, principal);
    }

    @MessageExceptionHandler(InvalidBombException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidBomb() {
        return new ErrorMessage("Invalid bomb.");
    }
}
