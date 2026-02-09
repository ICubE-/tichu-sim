package com.icube.sim.tichu.game.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public interface Card {
    default int score() {
        return 0;
    }

    static List<Card> getDeck() {
        var cards = new ArrayList<Card>(56);

        Arrays.stream(CardSuit.values()).forEach(shape -> {
            IntStream.rangeClosed(2, 14).forEach(number -> {
                cards.add(new StandardCard(shape, number));
            });
        });

        cards.add(new SparrowCard());
        cards.add(new PhoenixCard());
        cards.add(new DragonCard());
        cards.add(new DogCard());

        assert cards.size() == 56;

        return cards;
    }
}
