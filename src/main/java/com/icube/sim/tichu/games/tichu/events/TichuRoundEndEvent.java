package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

import java.util.List;

@Getter
public class TichuRoundEndEvent extends TichuEvent {
    private final List<List<Integer>> scores;

    public TichuRoundEndEvent(List<List<Integer>> scores) {
        this.scores = scores;
    }
}
