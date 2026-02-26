package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

import java.util.List;

@Getter
public class TichuRoundEndEvent extends TichuEvent {
    private final List<int[]> scoresHistory;

    public TichuRoundEndEvent(List<int[]> scoresHistory) {
        this.scoresHistory = scoresHistory;
    }
}
