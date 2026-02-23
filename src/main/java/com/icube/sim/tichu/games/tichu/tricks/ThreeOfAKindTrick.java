package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.Cards;
import lombok.Getter;

import java.util.List;

@Getter
public class ThreeOfAKindTrick extends Trick {
    private final int rank;
    private final boolean isPhoenixUsed;

    public ThreeOfAKindTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() == 3;
        assert Cards.areDistinct(cards);

        isPhoenixUsed = Cards.containsPhoenix(cards);
        var standardCards = Cards.extractStandardCards(cards);
        if (isPhoenixUsed) {
            assert standardCards.size() == 2;
            assert standardCards.get(0).rank() == standardCards.get(1).rank();
        } else {
            assert standardCards.size() == 3;
            assert standardCards.get(0).rank() == standardCards.get(1).rank();
            assert standardCards.get(1).rank() == standardCards.get(2).rank();
        }
        rank = standardCards.get(0).rank();
    }

    @Override
    public TrickType getType() {
        return TrickType.THREE_OF_A_KIND;
    }

    public static boolean isThreeOfAKindTrick(List<Card> cards) {
        if (cards.size() != 3 || !Cards.areDistinct(cards)) {
            return false;
        }

        var standardCards = Cards.extractStandardCards(cards);
        if (Cards.containsPhoenix(cards)) {
            return standardCards.size() == 2
                    && standardCards.get(0).rank() == standardCards.get(1).rank();
        } else {
            return standardCards.size() == 3
                    && standardCards.get(0).rank() == standardCards.get(1).rank()
                    && standardCards.get(1).rank() == standardCards.get(2).rank();
        }
    }
}
