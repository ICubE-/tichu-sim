package com.icube.sim.tichu.game.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.game.exceptions.GameHasAlreadyStartedException;
import com.icube.sim.tichu.game.GameService;
import com.icube.sim.tichu.game.exceptions.InvalidTeamAssignmentException;
import com.icube.sim.tichu.rooms.InvalidMemberCountException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class GameStartController {
    private final GameService gameService;

    @MessageMapping("/rooms/{roomId}/game/start")
    public void start(@DestinationVariable("roomId") String roomId) {
        gameService.start(roomId);
    }

    @MessageExceptionHandler(InvalidMemberCountException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidMemberCount() {
        return new ErrorMessage("Invalid member count.");
    }

    @MessageExceptionHandler(GameHasAlreadyStartedException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleGamePlaying() {
        return new ErrorMessage("Game has already been playing.");
    }

    @MessageExceptionHandler(InvalidTeamAssignmentException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTeamAssignment() {
        return new ErrorMessage("Team assignment is invalid.");
    }
}
