package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.common.exceptions.InvalidGameRuleException;
import com.icube.sim.tichu.games.tichu.TichuRule;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.common.exceptions.GameHasAlreadyStartedException;
import com.icube.sim.tichu.games.common.exceptions.ImmutableGameRuleException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
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

    @MessageExceptionHandler(GameHasAlreadyStartedException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleGameHasAlreadyStarted() {
        return new ErrorMessage("Game has already been playing.");
    }

    @MessageExceptionHandler(ImmutableGameRuleException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleImmutableGameRule() {
        return new ErrorMessage("Game rule is now immutable.");
    }

    @MessageExceptionHandler(InvalidTeamAssignmentException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTeamAssignment() {
        return new ErrorMessage("Team assignment is invalid.");
    }

    @MessageExceptionHandler(InvalidGameRuleException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidGameRule() {
        return new ErrorMessage("Game rule is invalid.");
    }
}
