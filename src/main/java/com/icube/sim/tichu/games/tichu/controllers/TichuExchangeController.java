package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.ExchangeSend;
import com.icube.sim.tichu.games.common.exceptions.GameNotFoundException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidExchangeException;
import com.icube.sim.tichu.games.common.exceptions.InvalidTimeOfActionException;
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
public class TichuExchangeController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/tichu/exchange")
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
