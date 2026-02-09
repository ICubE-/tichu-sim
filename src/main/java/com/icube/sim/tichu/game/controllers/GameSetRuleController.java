package com.icube.sim.tichu.game.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.game.GameRule;
import com.icube.sim.tichu.game.GameService;
import com.icube.sim.tichu.game.exceptions.GameHasAlreadyStartedException;
import com.icube.sim.tichu.game.exceptions.ImmutableGameRuleException;
import com.icube.sim.tichu.game.exceptions.InvalidTeamAssignmentException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class GameSetRuleController {
    private final GameService gameService;

    @MessageMapping("/rooms/{roomId}/game/set-rule")
    public void setRule(@DestinationVariable("roomId") String roomId, @Payload GameRule rule) {
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
}
