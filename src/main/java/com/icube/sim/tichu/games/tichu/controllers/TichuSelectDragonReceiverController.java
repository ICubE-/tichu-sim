package com.icube.sim.tichu.games.tichu.controllers;

import com.icube.sim.tichu.games.tichu.TichuService;
import com.icube.sim.tichu.games.tichu.dtos.SelectDragonReceiverSend;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class TichuSelectDragonReceiverController {
    private final TichuService tichuService;

    @MessageMapping("/rooms/{roomId}/game/tichu/select-dragon-receiver")
    public void selectDragonReceiver(
            @DestinationVariable("roomId") String roomId,
            @Payload SelectDragonReceiverSend selectDragonReceiverSend,
            Principal principal
    ) {
        tichuService.selectDragonReceiver(roomId, selectDragonReceiverSend, principal);
    }
}
