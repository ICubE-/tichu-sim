package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

@Getter
public class TichuPhaseStartEvent extends TichuEvent {
    private final int firstPlayerIndex;

    public TichuPhaseStartEvent(int firstPlayerIndex) {
        this.firstPlayerIndex = firstPlayerIndex;
    }
}
