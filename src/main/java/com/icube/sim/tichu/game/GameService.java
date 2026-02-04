package com.icube.sim.tichu.game;

import com.icube.sim.tichu.game.dtos.GameMessage;
import com.icube.sim.tichu.rooms.Room;
import com.icube.sim.tichu.rooms.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GameService {
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
