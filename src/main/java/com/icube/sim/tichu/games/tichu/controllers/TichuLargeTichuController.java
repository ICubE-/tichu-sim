package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.LargeTichuSend;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTichuDeclarationException;
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
public class TichuLargeTichuController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/tichu/large-tichu")
    public void largeTichu(
            @DestinationVariable("roomId") String roomId,
            @Payload LargeTichuSend largeTichuSend,
            Principal principal
    ) {
        gameService.largeTichu(roomId, largeTichuSend, principal);
    }

    @MessageExceptionHandler(InvalidTichuDeclarationException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTichuDeclaration() {
        return new ErrorMessage("Invalid tichu declaration.");
    }
}
