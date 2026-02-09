package com.icube.sim.tichu.game.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.game.GameService;
import com.icube.sim.tichu.game.dtos.ExchangeSend;
import com.icube.sim.tichu.game.exceptions.GameNotFoundException;
import com.icube.sim.tichu.game.exceptions.InvalidExchangeException;
import com.icube.sim.tichu.game.exceptions.InvalidTimeOfActionException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@AllArgsConstructor
@Controller
public class GameExchangeController {
    private final GameService gameService;

    @MessageMapping("/rooms/{roomId}/game/exchange")
    public void exchange(
            @DestinationVariable("roomId") String roomId,
            @Payload ExchangeSend exchangeSend,
            Principal principal
    ) {
        gameService.exchange(roomId, exchangeSend, principal);
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

    @MessageExceptionHandler(InvalidExchangeException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidExchange() {
        return new ErrorMessage("Invalid exchange.");
    }
}
