package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.tricks.Trick;
import lombok.Getter;

@Getter
public class TichuPlayBombEvent extends TichuEvent {
    private final Long playerId;
    private final Trick bomb;

    public TichuPlayBombEvent(Long playerId, Trick bomb) {
        this.playerId = playerId;
        this.bomb = bomb;
    }
}
