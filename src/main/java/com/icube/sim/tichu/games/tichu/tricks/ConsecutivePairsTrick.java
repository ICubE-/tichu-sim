package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.Cards;
import lombok.Getter;

import java.util.List;

@Getter
public class ConsecutivePairsTrick extends Trick {
    private final int minRank;
    private final int maxRank;
    private final boolean isPhoenixUsed;

    public ConsecutivePairsTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);

        assert cards.size() > 2 && cards.size() % 2 == 0;
        assert Cards.areDistinct(cards);

        isPhoenixUsed = Cards.containsPhoenix(cards);
        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        if (isPhoenixUsed) {
            var isSparrowUsed = Cards.containsSparrow(cards);
            if (isSparrowUsed) {
                // e.g. P12233
                assert standardCards.size() == cards.size() - 2;
                var expectedRank = 2;
                var count = 0;
                for (var card : standardCards) {
                    assert card.rank() == expectedRank;
                    if (count == 0) {
                        count = 1;
                    } else {
                        expectedRank++;
                        count = 0;
                    }
                }

                minRank = 1;
            } else {
                // e.g. 22P344
                assert standardCards.size() == cards.size() - 1;
                var expectedRank = standardCards.getFirst().rank();
                var count = 0;
                var usedPhoenix = false;
                for (var card : standardCards) {
                    if (count == 0) {
                        assert card.rank() == expectedRank;
                        count = 1;
                    } else {
                        if (card.rank() == expectedRank) {
                            expectedRank++;
                            count = 0;
                        } else {
                            assert !usedPhoenix;
                            assert card.rank() == expectedRank + 1;
                            expectedRank++;
                            usedPhoenix = true;
                        }
                    }
                }

                minRank = standardCards.getFirst().rank();
            }
        } else {
            // e.g. 223344
            assert standardCards.size() == cards.size();
            var expectedRank = standardCards.getFirst().rank();
            var count = 0;
            for (var card : standardCards) {
                assert card.rank() == expectedRank;
                if (count == 0) {
                    count = 1;
                } else {
                    expectedRank++;
                    count = 0;
                }
            }

            minRank = standardCards.getFirst().rank();
        }

        maxRank = standardCards.getLast().rank();
    }

    @Override
    public TrickType getType() {
        return TrickType.CONSECUTIVE_PAIRS;
    }

    public int length() {
        return maxRank - minRank + 1;
    }

    public static boolean isConsecutivePairsTrick(List<Card> cards) {
        if (cards.size() <= 2 || cards.size() % 2 != 0 || !Cards.areDistinct(cards)) {
            return false;
        }

        var standardCards = Cards.sortedCards(Cards.extractStandardCards(cards));
        if (Cards.containsPhoenix(cards)) {
            if (Cards.containsSparrow(cards)) {
                // e.g. P12233
                if (standardCards.size() != cards.size() - 2) {
                    return false;
                }

                var expectedRank = 2;
                var count = 0;
                for (var card : standardCards) {
                    if (card.rank() != expectedRank) {
                        return false;
                    }

                    if (count == 0) {
                        count = 1;
                    } else {
                        expectedRank++;
                        count = 0;
                    }
                }
            } else {
                // e.g. 22P344
                if (standardCards.size() != cards.size() - 1) {
                    return false;
                }

                var expectedRank = standardCards.getFirst().rank();
                var count = 0;
                var usedPhoenix = false;
                for (var card : standardCards) {
                    if (count == 0) {
                        if (card.rank() != expectedRank) {
                            return false;
                        }
                        count = 1;
                    } else {
                        if (card.rank() == expectedRank) {
                            expectedRank++;
                            count = 0;
                        } else {
                            if (usedPhoenix || card.rank() != expectedRank + 1) {
                                return false;
                            }
                            expectedRank++;
                            usedPhoenix = true;
                        }
                    }
                }
            }
        } else {
            // e.g. 223344
            if (standardCards.size() != cards.size()) {
                return false;
            }

            var expectedRank = standardCards.getFirst().rank();
            var count = 0;
            for (var card : standardCards) {
                if (card.rank() != expectedRank) {
                    return false;
                }

                if (count == 0) {
                    count = 1;
                } else {
                    expectedRank++;
                    count = 0;
                }
            }

        }

        return true;
    }
}
