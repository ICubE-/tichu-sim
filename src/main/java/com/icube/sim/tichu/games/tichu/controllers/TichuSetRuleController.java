package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuRule;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class TichuSetRuleController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/tichu/set-rule")
    public void setRule(@DestinationVariable("roomId") String roomId, @Payload TichuRule rule) {
        gameService.setRule(roomId, rule);
    }

    @MessageExceptionHandler(InvalidTeamAssignmentException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTeamAssignment() {
        return new ErrorMessage("Team assignment is invalid.");
    }
}
