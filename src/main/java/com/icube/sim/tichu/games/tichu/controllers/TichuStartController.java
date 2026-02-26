package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.common.websocket.ErrorMessage;
import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import com.icube.sim.tichu.rooms.InvalidMemberCountException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class TichuStartController {
    private final TichuService gameService;

    @MessageMapping("/rooms/{roomId}/game/tichu/start")
    public void start(@DestinationVariable("roomId") String roomId) {
        gameService.start(roomId);
    }

    @MessageExceptionHandler(InvalidMemberCountException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidMemberCount() {
        return new ErrorMessage("Invalid member count.");
    }

    @MessageExceptionHandler(InvalidTeamAssignmentException.class)
    @SendToUser("/queue/errors")
    public ErrorMessage handleInvalidTeamAssignment() {
        return new ErrorMessage("Team assignment is invalid.");
    }
}
