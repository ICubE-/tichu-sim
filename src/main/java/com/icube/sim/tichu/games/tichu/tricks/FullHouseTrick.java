package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.Cards;
import lombok.Getter;

import java.util.List;

@Getter
public class FullHouseTrick extends Trick {
    private final int rank;
    private final boolean isPhoenixUsed;

    public FullHouseTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() == 5;
        assert Cards.areDistinct(cards);

        isPhoenixUsed = Cards.containsPhoenix(cards);
        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        if (isPhoenixUsed) {
            var isSparrowUsed = Cards.containsSparrow(cards);
            if (isSparrowUsed) {
                // e.g. P1222
                assert standardCards.size() == 3;
                assert standardCards.get(0).rank() == standardCards.get(1).rank();
                assert standardCards.get(1).rank() == standardCards.get(2).rank();
                rank = standardCards.get(0).rank();
            } else {
                assert standardCards.size() == 4;
                // Reject 2222P
                assert standardCards.get(0).rank() != standardCards.get(3).rank();
                if (standardCards.get(0).rank() == standardCards.get(2).rank()) {
                    // e.g. 2223P
                    assert standardCards.get(0).rank() == standardCards.get(1).rank();
                    rank = standardCards.get(0).rank();
                } else {
                    // e.g. 2233P or P2333
                    assert standardCards.get(2).rank() == standardCards.get(3).rank();
                    assert standardCards.get(0).rank() == standardCards.get(1).rank() ||
                            standardCards.get(1).rank() == standardCards.get(2).rank();
                    rank = standardCards.get(3).rank();
                }
            }
        } else {
            assert standardCards.size() == 5;
            assert standardCards.get(0).rank() == standardCards.get(1).rank();
            assert standardCards.get(3).rank() == standardCards.get(4).rank();
            if (standardCards.get(1).rank() == standardCards.get(2).rank()) {
                // e.g. 22233
                rank = standardCards.get(0).rank();
            } else {
                // e.g. 22333
                assert standardCards.get(2).rank() == standardCards.get(3).rank();
                rank = standardCards.get(4).rank();
            }
        }
    }

    @Override
    public TrickType getType() {
        return TrickType.FULL_HOUSE;
    }

    public static boolean isFullHouseTrick(List<Card> cards) {
        if (cards.size() != 5 || !Cards.areDistinct(cards)) {
            return false;
        }

        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        if (Cards.containsPhoenix(cards)) {
            if (Cards.containsSparrow(cards)) {
                // e.g. P1222
                return standardCards.size() == 3
                        && standardCards.get(0).rank() == standardCards.get(1).rank()
                        && standardCards.get(1).rank() == standardCards.get(2).rank();
            } else {
                // Reject 2222P
                if (standardCards.size() != 4 || standardCards.get(0).rank() == standardCards.get(3).rank()) {
                    return false;
                }

                // e.g. 2223P
                return standardCards.get(0).rank() == standardCards.get(1).rank()
                        && standardCards.get(1).rank() == standardCards.get(2).rank()
                        // e.g. 2233P
                        || standardCards.get(0).rank() == standardCards.get(1).rank()
                        && standardCards.get(2).rank() == standardCards.get(3).rank()
                        // e.g. P2333
                        || standardCards.get(1).rank() == standardCards.get(2).rank()
                        && standardCards.get(2).rank() == standardCards.get(3).rank();
            }
        } else {
            return standardCards.size() == 5
                    && standardCards.get(0).rank() == standardCards.get(1).rank()
                    && standardCards.get(3).rank() == standardCards.get(4).rank()
                    // e.g. 22233
                    && (standardCards.get(1).rank() == standardCards.get(2).rank()
                            // e.g. 22333
                            || standardCards.get(2).rank() == standardCards.get(3).rank());
        }
    }
}
