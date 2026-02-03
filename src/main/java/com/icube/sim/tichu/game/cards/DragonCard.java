package com.icube.sim.tichu.game.cards;

public record DragonCard() implements Card {
    @Override
    public int score() {
        return 25;
    }
}
