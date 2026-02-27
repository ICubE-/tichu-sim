package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.TichuMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class TichuGetController {
    private final TichuService tichuService;

    @MessageMapping("/rooms/{roomId}/game/tichu/get")
    public TichuMessage get(
            @DestinationVariable("roomId") String roomId,
            Principal principal
    ) {
        var tichuDto = tichuService.get(roomId, principal);
        return TichuMessage.get(tichuDto);
    }
}
