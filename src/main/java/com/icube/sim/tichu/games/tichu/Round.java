package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.dtos.LargeTichuSend;
import com.icube.sim.tichu.games.tichu.events.TichuFirstDrawEvent;
import com.icube.sim.tichu.games.tichu.events.TichuLargeTichuEvent;
import com.icube.sim.tichu.games.tichu.events.TichuSecondDrawEvent;
import com.icube.sim.tichu.games.tichu.events.TichuSmallTichuEvent;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTichuDeclarationException;
import com.icube.sim.tichu.games.common.exceptions.InvalidTimeOfActionException;

import java.util.*;

public class Round {
    private RoundStatus status;
    private final Tichu game;
    private final List<Card> deck;
    private final TichuDeclaration[] tichuDeclarations;
    private final ExchangePhase exchangePhase;
    private final List<Phase> phases;

    public Round(Tichu game) {
        this.game = game;
        this.tichuDeclarations = new TichuDeclaration[] { null, null, null, null };
        this.exchangePhase = new ExchangePhase(game, this);
        this.phases = new ArrayList<>();

        this.deck = Card.getDeck();
        Collections.shuffle(deck);
        assert deck.size() == 56;

        doFirstDraw();

        this.status = RoundStatus.WAITING_LARGE_TICHU;
    }

    private void doFirstDraw() {
        var firstDraws = new HashMap<Long, List<Card>>();

        for (var i = 0; i < 4; i++) {
            var player = game.getPlayer(i);
            player.initFirstDraws(deck.subList(i * 8, (i + 1) * 8));
            assert player.getHandSize() == 8;
            firstDraws.put(player.getId(), player.getHand());
        }

        game.addEvent(new TichuFirstDrawEvent(firstDraws));
    }

    public void largeTichu(Long playerId, LargeTichuSend largeTichuSend) {
        if (status != RoundStatus.WAITING_LARGE_TICHU) {
            throw new InvalidTimeOfActionException();
        }

        var playerIndex = game.getPlayerIndexById(playerId);
        if (tichuDeclarations[playerIndex] != null) {
            throw new InvalidTichuDeclarationException();
        }
        tichuDeclarations[playerIndex] = largeTichuSend.getIsLargeTichuDeclared() ?
                TichuDeclaration.LARGE : TichuDeclaration.NONE;


        game.addEvent(new TichuLargeTichuEvent(tichuDeclarations));

        if (Arrays.stream(tichuDeclarations).allMatch(Objects::nonNull)) {
            doSecondDraw();
        }
    }

    private void doSecondDraw() {
        var hands = new HashMap<Long, List<Card>>();

        for (var i = 0; i < 4; i++) {
            var player = game.getPlayer(i);
            player.addSecondDraws(deck.subList(32 + i * 6, 32 + (i + 1) * 6));
            assert player.getHandSize() == 14;
            hands.put(player.getId(), player.getHand());
        }

        game.addEvent(new TichuSecondDrawEvent(hands));

        status = RoundStatus.EXCHANGING;
    }

    public void smallTichu(Long playerId) {
        if (status == RoundStatus.WAITING_LARGE_TICHU || status == RoundStatus.FINISHED) {
            throw new InvalidTimeOfActionException();
        }

        var playerIndex = game.getPlayerIndexById(playerId);
        var player = game.getPlayer(playerIndex);
        if (player.getHandSize() != 14 || tichuDeclarations[playerIndex] != TichuDeclaration.NONE) {
            throw new InvalidTichuDeclarationException();
        }

        tichuDeclarations[playerIndex] = TichuDeclaration.SMALL;
        game.addEvent(new TichuSmallTichuEvent(player.getId()));
    }

    public ExchangePhase getExchangePhase() {
        if (status != RoundStatus.EXCHANGING) {
            throw new InvalidTimeOfActionException();
        }

        return exchangePhase;
    }

    public void finishExchangePhase() {
        assert status == RoundStatus.EXCHANGING;
        status = RoundStatus.PLAYING;
        nextPhase();
    }

    public void nextPhase() {
    }
}
