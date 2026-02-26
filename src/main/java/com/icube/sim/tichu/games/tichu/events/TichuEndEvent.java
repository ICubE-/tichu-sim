package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

import java.util.List;

@Getter
public class TichuEndEvent extends TichuEvent {
    private final List<int[]> scoresHistory;

    public TichuEndEvent(List<int[]> scoresHistory) {
        this.scoresHistory = scoresHistory;
    }
}
