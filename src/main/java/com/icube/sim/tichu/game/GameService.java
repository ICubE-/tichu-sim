package com.icube.sim.tichu.game;

import com.icube.sim.tichu.game.dtos.ExchangeSend;
import com.icube.sim.tichu.game.dtos.GameMessage;
import com.icube.sim.tichu.game.dtos.LargeTichuSend;
import com.icube.sim.tichu.game.exceptions.InvalidTeamAssignmentException;
import com.icube.sim.tichu.rooms.Room;
import com.icube.sim.tichu.rooms.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;

@AllArgsConstructor
@Service
public class GameService {
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void setRule(String roomId, GameRule rule) {
        if (rule.getTeamAssignment().size() > 4) {
            throw new InvalidTeamAssignmentException();
        }

        var room = roomRepository.findById(roomId).orElseThrow();
        room.setGameRule(rule);

        publishMessage(GameMessage.setRule(rule), room);
    }

    public void start(String roomId) {
        var room = roomRepository.findById(roomId).orElseThrow();
        room.startGame();

        var game = room.getGame();
        publishMessages(game, room);
    }

    public void largeTichu(String roomId, LargeTichuSend largeTichuSend, Principal principal) {
        var room = roomRepository.findById(roomId).orElseThrow();
        var game = room.getGame();

        var round = game.getCurrentRound();
        round.largeTichu(Long.valueOf(principal.getName()), largeTichuSend);

        publishMessages(game, room);
    }

    public void smallTichu(String roomId, Principal principal) {
        var room = roomRepository.findById(roomId).orElseThrow();
        var game = room.getGame();

        var round = game.getCurrentRound();
        round.smallTichu(Long.valueOf(principal.getName()));

        publishMessages(game, room);
    }

    public void exchange(String roomId, ExchangeSend exchangeSend, Principal principal) {
        var room = roomRepository.findById(roomId).orElseThrow();
        var game = room.getGame();

        var exchangePhase = game.getCurrentRound().getExchangePhase();
        exchangePhase.queueExchange(Long.valueOf(principal.getName()), exchangeSend);

        publishMessages(game, room);
    }

    private void publishMessages(Game game, Room room) {
        var message = game.dequeueMessage();
        while (message != null) {
            publishMessage(message, room);
            message = game.dequeueMessage();
        }
    }

    private void publishMessage(GameMessage message, Room room) {
        var targetUserId = message.getTargetUserId();
        if (targetUserId != null) {
            messagingTemplate.convertAndSendToUser(targetUserId.toString(), "/queue/game", message);
        } else {
            for (var userId : room.getMembers().keySet()) {
                messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/game", message);
            }
        }
    }
}
