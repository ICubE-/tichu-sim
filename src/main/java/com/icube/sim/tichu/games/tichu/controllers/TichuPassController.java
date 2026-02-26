package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.games.tichu.TichuService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class TichuPassController {
    private final TichuService tichuService;

    @MessageMapping("/rooms/{roomId}/game/tichu/pass")
    public void pass(@DestinationVariable("roomId") String roomId, Principal principal) {
        tichuService.pass(roomId, principal);
    }
}
