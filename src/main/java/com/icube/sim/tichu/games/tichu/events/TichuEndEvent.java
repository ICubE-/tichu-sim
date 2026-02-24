package com.icube.sim.tichu.games.tichu.events;

import lombok.Getter;

import java.util.List;

@Getter
public class TichuEndEvent extends TichuEvent {
    private final List<List<Integer>> scores;

    public TichuEndEvent(List<List<Integer>> scores) {
        this.scores = scores;
    }
}
