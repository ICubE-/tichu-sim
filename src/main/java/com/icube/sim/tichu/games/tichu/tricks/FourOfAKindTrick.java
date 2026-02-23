package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.Cards;
import lombok.Getter;

import java.util.List;

@Getter
public class FourOfAKindTrick extends Trick {
    private final int rank;

    public FourOfAKindTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() == 4;
        assert Cards.areDistinct(cards);

        var standardCards = Cards.extractStandardCards(cards);
        assert standardCards.size() == 4;
        assert standardCards.get(0).rank() == standardCards.get(1).rank();
        assert standardCards.get(1).rank() == standardCards.get(2).rank();
        assert standardCards.get(2).rank() == standardCards.get(3).rank();

        rank = standardCards.get(0).rank();
    }

    @Override
    public TrickType getType() {
        return TrickType.FOUR_OF_A_KIND;
    }

    public static boolean isFourOfAKindTrick(List<Card> cards) {
        if (cards.size() != 4 || !Cards.areDistinct(cards)) {
            return false;
        }

        var standardCards = Cards.extractStandardCards(cards);
        return standardCards.size() == 4
                && standardCards.get(0).rank() == standardCards.get(1).rank()
                && standardCards.get(1).rank() == standardCards.get(2).rank()
                && standardCards.get(2).rank() == standardCards.get(3).rank();
    }
}
