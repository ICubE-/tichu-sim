package com.icube.sim.tichu.game.cards;

public record StandardCard(CardSuit suit, int rank) implements Card {
    public StandardCard {
        assert rank >= 2 && rank <= 14;
    }

    @Override
    public int score() {
        return switch (rank) {
            case 5 -> 5;
            case 10, 13 -> 10;
            default -> 0;
        };
    }
}
