package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

@Getter
public class TichuSelectDragonReceiverEvent extends TichuEvent {
    private final Long receiverId;

    public TichuSelectDragonReceiverEvent(Long receiverId) {
        this.receiverId = receiverId;
    }
}
