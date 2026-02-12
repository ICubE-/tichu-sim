package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

@Getter
public class TichuSmallTichuEvent extends TichuEvent {
    private final Long playerId;

    public TichuSmallTichuEvent(Long playerId) {
        this.playerId = playerId;
    }
}
