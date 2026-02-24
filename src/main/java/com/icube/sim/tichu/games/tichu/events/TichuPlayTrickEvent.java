package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.tricks.Trick;
import lombok.Getter;

@Getter
public class TichuPlayTrickEvent extends TichuEvent {
    private final Long playerId;
    private final Trick trick;

    public TichuPlayTrickEvent(Long playerId, Trick trick) {
        this.playerId = playerId;
        this.trick = trick;
    }
}
