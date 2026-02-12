package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.Tichu;
import com.icube.sim.tichu.games.tichu.cards.Card;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class TichuExchangeEvent extends TichuEvent {
    private final Map<Long, Integer> playerIndexById;
    private final Card[][] exchangingCards;

    public TichuExchangeEvent(Map<Long, Integer> playerIndexById, Card[][] exchangingCards) {
        this.playerIndexById = Map.copyOf(playerIndexById);
        this.exchangingCards = exchangingCards;
    }

    public static TichuExchangeEvent of(Tichu game, Card[][] exchangingCards) {
        var playerIndexById = Map.of(
                game.getPlayer(0).getId(), 0,
                game.getPlayer(1).getId(), 1,
                game.getPlayer(2).getId(), 2,
                game.getPlayer(3).getId(), 3
        );
        return new TichuExchangeEvent(playerIndexById, exchangingCards);
    }

    public Card[] getCardsGaveFrom(Long playerId) {
        return exchangingCards[playerIndexById.get(playerId)];
    }

    public Card getCardGaveLeftFrom(Long playerId) {
        return getCardsGaveFrom(playerId)[2];
    }

    public Card getCardGaveMidFrom(Long playerId) {
        return getCardsGaveFrom(playerId)[1];
    }

    public Card getCardGaveRightFrom(Long playerId) {
        return getCardsGaveFrom(playerId)[0];
    }

    public Card getCardReceivedFromLeft(Long playerId) {
        return exchangingCards[(playerIndexById.get(playerId) + 3) % 4][0];
    }

    public Card getCardReceivedFromMid(Long playerId) {
        return exchangingCards[(playerIndexById.get(playerId) + 2) % 4][1];
    }

    public Card getCardReceivedFromRight(Long playerId) {
        return exchangingCards[(playerIndexById.get(playerId) + 1) % 4][2];
    }

    public List<Card> getCardsReceived(Long playerId) {
        return List.of(
                getCardReceivedFromLeft(playerId),
                getCardReceivedFromMid(playerId),
                getCardReceivedFromRight(playerId)
        );
    }
}
