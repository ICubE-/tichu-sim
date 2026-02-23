package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.Cards;
import lombok.Getter;

import java.util.List;

@Getter
public class StraightFlushTrick extends Trick {
    private final int minRank;
    private final int maxRank;

    protected StraightFlushTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() >= 5 && cards.size() <= 13;
        assert Cards.areDistinct(cards);

        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        assert standardCards.size() == cards.size();

        var expectedSuit = standardCards.getFirst().suit();
        var expectedRank = standardCards.getFirst().rank();
        for (var card : standardCards) {
            assert card.suit() == expectedSuit;
            assert card.rank() == expectedRank;
            expectedRank++;
        }

        minRank = standardCards.getFirst().rank();
        maxRank = standardCards.getLast().rank();
    }

    @Override
    public TrickType getType() {
        return TrickType.STRAIGHT_FLUSH;
    }

    public int length() {
        return maxRank - minRank + 1;
    }

    public static boolean isStraightFlushTrick(List<Card> cards) {
        if (cards.size() < 5 || cards.size() > 13 || !Cards.areDistinct(cards)) {
            return false;
        }

        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        if (standardCards.size() != cards.size()) {
            return false;
        }

        var expectedSuit = standardCards.getFirst().suit();
        var expectedRank = standardCards.getFirst().rank();
        for (var card : standardCards) {
            if (card.suit() != expectedSuit || card.rank() != expectedRank) {
                return false;
            }
            expectedRank++;
        }

        return true;
    }
}
