package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.common.domain.Game;
import com.icube.sim.tichu.games.common.domain.GameRule;
import com.icube.sim.tichu.games.common.event.GameSetRuleEvent;
import com.icube.sim.tichu.games.common.service.AbstractGameService;
import com.icube.sim.tichu.games.tichu.dtos.ExchangeSend;
import com.icube.sim.tichu.games.tichu.dtos.LargeTichuSend;
import com.icube.sim.tichu.games.tichu.events.TichuSetRuleEvent;
import com.icube.sim.tichu.games.tichu.exceptions.InvalidTeamAssignmentException;
import com.icube.sim.tichu.rooms.Room;
import com.icube.sim.tichu.rooms.RoomRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class TichuService extends AbstractGameService {
    public TichuService(RoomRepository roomRepository, ApplicationEventPublisher eventPublisher) {
        super(roomRepository, eventPublisher);
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
        round.largeTichu(Long.valueOf(principal.getName()), isLargeTichuDeclared);

        publishQueuedEvents(game, roomId);
    }

    public void smallTichu(String roomId, Principal principal) {
        var game = getGame(roomId);
        var round = game.getCurrentRound();
        round.smallTichu(Long.valueOf(principal.getName()));

        publishQueuedEvents(game, roomId);
    }

    public void exchange(String roomId, ExchangeSend exchangeSend, Principal principal) {
        var game = getGame(roomId);
        var exchangePhase = game.getCurrentRound().getExchangePhase();
        exchangePhase.queueExchange(Long.valueOf(principal.getName()), exchangeSend);

        publishQueuedEvents(game, roomId);
    }

    @Override
    protected Tichu getGame(String roomId) {
        return (Tichu) super.getGame(roomId);
    }
}
