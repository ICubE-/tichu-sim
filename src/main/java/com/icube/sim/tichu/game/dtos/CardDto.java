package com.icube.sim.tichu.game.dtos;

import com.icube.sim.tichu.game.cards.CardSuit;
import lombok.Data;

@Data
public class CardDto {
    private CardType type;
    private CardSuit suit;
    private Integer rank;

    public CardDto(CardType type) {
        this(type, null, null);
    }

    public CardDto(CardType type, CardSuit suit, Integer rank) {
        this.type = type;
        this.suit = suit;
        this.rank = rank;
    }
}
