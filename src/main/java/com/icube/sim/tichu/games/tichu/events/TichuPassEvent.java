package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

@Getter
public class TichuPassEvent extends TichuEvent {
    private final Long playerId;

    public TichuPassEvent(Long playerId) {
        this.playerId = playerId;
    }
}
