package com.icube.sim.tichu.games.tichu.tricks;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.cards.DogCard;

import java.util.List;

public class DogTrick extends Trick {
    public DogTrick(int playerIndex, List<Card> cards) {
        super(playerIndex, cards);
        assert cards.size() == 1;
        assert cards.get(0) instanceof DogCard;
    }

    @Override
    public TrickType getType() {
        return TrickType.DOG;
    }
}
