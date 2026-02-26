package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class Trick {
    @Getter
    private final int playerIndex;
    @Getter
    protected final List<Card> cards;

    protected Trick(int playerIndex, List<Card> cards) {
        this.playerIndex = playerIndex;
        this.cards = cards;
    }

    public abstract TrickType getType();

    public abstract boolean canCoverUp(@Nullable Trick other);

    public abstract boolean canPlayWishCardAfter(int wish, List<Card> hand);
}
