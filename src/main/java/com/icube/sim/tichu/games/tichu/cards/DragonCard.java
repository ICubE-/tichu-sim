package com.icube.sim.tichu.games.tichu.cards;

public record DragonCard() implements Card {
    @Override
    public int score() {
        return 25;
    }
}
