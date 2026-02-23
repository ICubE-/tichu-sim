package com.icube.sim.tichu.games.tichu.tricks;

public enum TrickType {
    SINGLE,
    PAIR,
    CONSECUTIVE_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    STRAIGHT,
    DOG,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH,
    ;

    public boolean isBomb() {
        return this == FOUR_OF_A_KIND || this == STRAIGHT_FLUSH;
    }
}
