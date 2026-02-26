package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.exceptions.CardMappingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(basePackages = "com.icube.sim.tichu.games.tichu.controllers")
public class TichuExceptionHandler {
    @MessageExceptionHandler(CardMappingException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleCardMapping() {
        return new ErrorMessage("Failed to parse card.");
    }
}
