package com.icube.sim.tichu.games.tichu.cards;

public interface Card {
    default int score() {
        return 0;
    }
}
