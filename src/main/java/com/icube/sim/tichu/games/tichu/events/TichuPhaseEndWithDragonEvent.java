package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

@Getter
public class TichuPhaseEndWithDragonEvent extends TichuEvent {
    private final int playerIndex;

    public TichuPhaseEndWithDragonEvent(int playerIndex) {
        this.playerIndex = playerIndex;
    }
}
