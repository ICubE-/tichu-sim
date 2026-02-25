package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTichuDeclarationException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@AllArgsConstructor
@Controller
public class TichuSmallTichuController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/tichu/small-tichu")
    public void smallTichu(
            @DestinationVariable("roomId") String roomId,
            Principal principal
    ) {
        gameService.smallTichu(roomId, principal);
    }

    @MessageExceptionHandler(InvalidTichuDeclarationException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTichuDeclaration() {
        return new ErrorMessage("Invalid tichu declaration.");
    }
}
