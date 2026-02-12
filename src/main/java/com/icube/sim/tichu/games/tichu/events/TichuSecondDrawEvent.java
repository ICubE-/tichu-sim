package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.cards.Card;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class TichuSecondDrawEvent extends TichuEvent {
    private final Map<Long, List<Card>> hands;

    public TichuSecondDrawEvent(Map<Long, List<Card>> hands) {
        this.hands = hands;
    }
}
