package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.dtos.ExchangeMessage;
import com.icube.sim.tichu.games.tichu.dtos.ExchangeSend;
import com.icube.sim.tichu.games.tichu.dtos.GameMessage;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidExchangeException;
import com.icube.sim.tichu.games.tichu.mappers.CardMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExchangePhase {
    private final Game game;
    private final Round round;
    // exchangingCards[i][j] = card to be exchanged from player i to player ((i + j + 1) % 4)
    private final Card[][] exchangingCards;

    public ExchangePhase(Game game, Round round) {
        this.game = game;
        this.round = round;
        this.exchangingCards = new Card[4][3];
    }

    public void queueExchange(Long playerId, ExchangeSend exchangeSend) {
        var playerIndex = game.getPlayerIndexById(playerId);

        var cardMapper = new CardMapper();
        var leftCard = cardMapper.toCardNullable(exchangeSend.getLeft());
        var midCard = cardMapper.toCardNullable(exchangeSend.getMid());
        var rightCard = cardMapper.toCardNullable(exchangeSend.getRight());

        checkExchange(leftCard, midCard, rightCard, game.getPlayer(playerIndex));

        exchangingCards[playerIndex][0] = rightCard;
        exchangingCards[playerIndex][1] = midCard;
        exchangingCards[playerIndex][2] = leftCard;

        if (isExchangeFullyQueued()) {
            doExchange();
        }
    }

    public void checkExchange(Card leftCard, Card midCard, Card rightCard, Player player) {
        if (leftCard == null && midCard == null && rightCard == null) {
            throw new InvalidExchangeException();
        }
        if (leftCard != null && (leftCard.equals(midCard) || leftCard.equals(rightCard) || !player.hasCard(leftCard))) {
            throw new InvalidExchangeException();
        }
        if (midCard != null && (midCard.equals(rightCard) || !player.hasCard(midCard))) {
            throw new InvalidExchangeException();
        }
        if (rightCard != null && !player.hasCard(rightCard)) {
            throw new InvalidExchangeException();
        }
    }

    public boolean isExchangeFullyQueued() {
        return Arrays.stream(exchangingCards).allMatch(playerExchangeCards ->
                        Arrays.stream(playerExchangeCards).allMatch(Objects::nonNull));
    }

    public void doExchange() {
        assert isExchangeFullyQueued();

        for (var i = 0; i < 4; i++) {
            var player = game.getPlayer(i);
            var cardsGave = exchangingCards[i];

            var cardReceivedFromRight = exchangingCards[(i + 1) % 4][2];
            var cardReceivedFromMid = exchangingCards[(i + 2) % 4][1];
            var cardReceivedFromLeft = exchangingCards[(i + 3) % 4][0];
            var cardsReceived = List.of(cardReceivedFromRight, cardReceivedFromMid, cardReceivedFromLeft);
            player.exchange(List.of(cardsGave), cardsReceived);

            var cardMapper = new CardMapper();
            var exchangeMessage = new ExchangeMessage(
                    cardMapper.toDto(cardReceivedFromLeft),
                    cardMapper.toDto(cardReceivedFromMid),
                    cardMapper.toDto(cardReceivedFromRight)
            );
            game.enqueueMessage(GameMessage.exchange(player.getId(), exchangeMessage));
        }

        round.finishExchangePhase();
    }
}
