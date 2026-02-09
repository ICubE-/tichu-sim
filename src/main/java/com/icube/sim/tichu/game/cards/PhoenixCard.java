package com.icube.sim.tichu.game.cards;

public record PhoenixCard() implements Card {
    @Override
    public int score() {
        return -25;
    }
}
