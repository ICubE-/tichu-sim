package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.*;
import lombok.Getter;

import java.util.List;

@Getter
public class PairTrick extends Trick {
    private final int rank;
    private final boolean isPhoenixUsed;

    public PairTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() == 2;
        assert Cards.areDistinct(cards);

        isPhoenixUsed = Cards.containsPhoenix(cards);
        if (isPhoenixUsed) {
            var nonPhoenixCard = Cards.extractNonPhoenixCards(cards).get(0);
            if (nonPhoenixCard instanceof SparrowCard) {
                rank = 1;
            } else if (nonPhoenixCard instanceof StandardCard standardCard) {
                rank = standardCard.rank();
            } else {
                throw new AssertionError();
            }
        } else {
            var standardCards = Cards.extractStandardCards(cards);
            assert standardCards.size() == 2;
            assert standardCards.get(0).rank() == standardCards.get(1).rank();
            rank = standardCards.get(0).rank();
        }
    }

    @Override
    public TrickType getType() {
        return TrickType.PAIR;
    }

    public static boolean isPairTrick(List<Card> cards) {
        if (cards.size() != 2 || !Cards.areDistinct(cards)) {
            return false;
        }

        if (Cards.containsPhoenix(cards)) {
            var nonPhoenixCard = Cards.extractNonPhoenixCards(cards).get(0);
            return nonPhoenixCard instanceof SparrowCard || nonPhoenixCard instanceof StandardCard;
        } else {
            var standardCards = Cards.extractStandardCards(cards);
            return standardCards.size() == 2 && standardCards.get(0).rank() == standardCards.get(1).rank();
        }
    }
}
