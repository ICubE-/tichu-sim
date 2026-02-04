package com.icube.sim.tichu.game;

import com.icube.sim.tichu.game.cards.Card;
import com.icube.sim.tichu.game.dtos.GameMessage;

import java.util.*;

public class Round {
    private RoundStatus status;
    private final Game game;
    private final List<Card> deck;
    private final TichuDeclaration[] tichuDeclarations;
    private final ExchangePhase exchangePhase;
    private final List<Phase> phases;

    public Round(Game game) {
        this.game = game;
        this.tichuDeclarations = new TichuDeclaration[] {null, null, null, null};
        this.exchangePhase = new ExchangePhase(game, this);
        this.phases = new ArrayList<>();

        this.deck = Card.getDeck();
        Collections.shuffle(deck);
        assert deck.size() == 56;

        doFirstDraw();

        this.status = RoundStatus.WAITING_LARGE_TICHU;
    }

    private void doFirstDraw() {
        for (var i = 0; i < 4; i++) {
            var player = game.getPlayer(i);
            player.initFirstDraws(deck.subList(i * 8, (i + 1) * 8));
            assert player.getHand().size() == 8;

            game.enqueueMessage(GameMessage.initFirstDraws(player.getId(), player.getHand()));
        }
    }
}
