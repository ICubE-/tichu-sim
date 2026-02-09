package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.GameService;
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
public class GameSmallTichuController {
    private final GameService gameService;

    @MessageMapping("/rooms/{roomId}/game/small-tichu")
    public void smallTichu(
            @DestinationVariable("roomId") String roomId,
            Principal principal
    ) {
        gameService.smallTichu(roomId, principal);
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
