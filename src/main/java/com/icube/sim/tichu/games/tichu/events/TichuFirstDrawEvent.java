package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.cards.Card;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class TichuFirstDrawEvent extends TichuEvent {
    private final Map<Long, List<Card>> firstDraws;

    public TichuFirstDrawEvent(Map<Long, List<Card>> firstDraws) {
        this.firstDraws = firstDraws;
    }
}
