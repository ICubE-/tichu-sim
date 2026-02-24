package com.icube.sim.tichu.games.tichu.tricks;

import lombok.Getter;

@Getter
public enum TrickType {
    SINGLE(false),
    PAIR(false),
    CONSECUTIVE_PAIRS(false),
    THREE_OF_A_KIND(false),
    FULL_HOUSE(false),
    STRAIGHT(false),
    DOG(false),
    FOUR_OF_A_KIND(true),
    STRAIGHT_FLUSH(true),
    ;

    private final boolean isBomb;

    TrickType(boolean isBomb) {
        this.isBomb = isBomb;
    }
}
