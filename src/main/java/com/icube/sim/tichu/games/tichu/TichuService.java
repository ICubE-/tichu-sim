package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.common.domain.Game;
import com.icube.sim.tichu.games.common.domain.GameRule;
import com.icube.sim.tichu.games.common.event.GameSetRuleEvent;
import com.icube.sim.tichu.games.common.service.AbstractGameService;
import com.icube.sim.tichu.games.tichu.dtos.*;
import com.icube.sim.tichu.games.tichu.events.TichuSetRuleEvent;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTichuDeclarationException;
import com.icube.sim.tichu.games.tichu.mappers.CardMapper;
import com.icube.sim.tichu.rooms.Room;
import com.icube.sim.tichu.rooms.RoomRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class TichuService extends AbstractGameService {
    private final CardMapper cardMapper;

    public TichuService(RoomRepository roomRepository, ApplicationEventPublisher eventPublisher) {
        super(roomRepository, eventPublisher);
        cardMapper = new CardMapper();
    }

    @Override
    protected void checkRule(GameRule gameRule) {
        assert gameRule instanceof TichuRule;

        var tichuRule = (TichuRule) gameRule;
        if (tichuRule.getTeamAssignment().size() > 4) {
            throw new InvalidTeamAssignmentException();
        }
    }

    @Override
    protected GameSetRuleEvent createSetRuleEvent(GameRule gameRule) {
        return new TichuSetRuleEvent((TichuRule) gameRule);
    }

    @Override
    protected void postStart(Game game, Room room) {
        // Empty
    }

    public void largeTichu(String roomId, LargeTichuSend largeTichuSend, Principal principal) {
        var isLargeTichuDeclared = largeTichuSend.getIsLargeTichuDeclared();
        if (isLargeTichuDeclared == null) {
            throw new InvalidTichuDeclarationException();
        }

        var game = getGame(roomId);
        var round = game.getCurrentRound();
        round.largeTichu(getPlayerId(principal), isLargeTichuDeclared);

        publishQueuedEvents(game, roomId);
    }

    public void smallTichu(String roomId, Principal principal) {
        var game = getGame(roomId);
        var round = game.getCurrentRound();
        round.smallTichu(getPlayerId(principal));

        publishQueuedEvents(game, roomId);
    }

    public void exchange(String roomId, ExchangeSend exchangeSend, Principal principal) {
        var game = getGame(roomId);
        var exchangePhase = game.getCurrentRound().getExchangePhase();
        exchangePhase.queueExchange(
                getPlayerId(principal),
                cardMapper.toCardNullable(exchangeSend.getLeft()),
                cardMapper.toCardNullable(exchangeSend.getMid()),
                cardMapper.toCardNullable(exchangeSend.getRight())
        );

        publishQueuedEvents(game, roomId);
    }

    public void playTrick(String roomId, TrickSend trickSend, Principal principal) {
        var game = getGame(roomId);
        var phase = game.getCurrentRound().getCurrentPhase();
        phase.playTrick(
                getPlayerId(principal),
                cardMapper.toCards(trickSend.getCards()),
                trickSend.getWish()
        );
    }

    public void playBomb(String roomId, BombSend bombSend, Principal principal) {
        var game = getGame(roomId);
        var phase = game.getCurrentRound().getCurrentPhase();
        phase.playBomb(getPlayerId(principal), cardMapper.toCards(bombSend.getCards()));
    }

    public void pass(String roomId, Principal principal) {
        var game = getGame(roomId);
        var phase = game.getCurrentRound().getCurrentPhase();
        phase.pass(getPlayerId(principal));
    }

    public void selectDragonReceiver(
            String roomId,
            SelectDragonReceiverSend selectDragonReceiverSend,
            Principal principal
    ) {
        var game = getGame(roomId);
        var phase = game.getCurrentRound().getCurrentPhase();
        phase.selectDragonReceiver(getPlayerId(principal), selectDragonReceiverSend.getGiveRight());
    }

    @Override
    protected Tichu getGame(String roomId) {
        return (Tichu) super.getGame(roomId);
    }

    private static @NonNull Long getPlayerId(Principal principal) {
        return Long.valueOf(principal.getName());
    }
}
