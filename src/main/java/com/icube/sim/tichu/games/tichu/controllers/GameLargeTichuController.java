package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.LargeTichuSend;
import com.icube.sim.tichu.games.tichu.exceptions.GameNotFoundException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTichuDeclarationException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTimeOfActionException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@AllArgsConstructor
@Controller
public class GameLargeTichuController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/large-tichu")
    public void largeTichu(
            @DestinationVariable("roomId") String roomId,
            @Payload LargeTichuSend largeTichuSend,
            Principal principal
    ) {
        gameService.largeTichu(roomId, largeTichuSend, principal);
    }

    @MessageExceptionHandler(GameNotFoundException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleGameNotFound() {
        return new ErrorMessage("Game not found.");
    }

    @MessageExceptionHandler(InvalidTimeOfActionException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTimeOfAction() {
        return new ErrorMessage("The action cannot be performed at this time.");
    }

    @MessageExceptionHandler(InvalidTichuDeclarationException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTichuDeclaration() {
        return new ErrorMessage("Invalid tichu declaration.");
    }
}
