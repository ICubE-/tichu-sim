package com.icube.sim.tichu.games.tichu.cards;

public record PhoenixCard() implements Card {
    @Override
    public int score() {
        return -25;
    }
}
