package com.icube.sim.tichu.games.tichu;

import com.icube.sim.tichu.games.tichu.dtos.ExchangeMessage;
import com.icube.sim.tichu.games.tichu.dtos.TichuMessage;
import com.icube.sim.tichu.games.tichu.events.*;
import com.icube.sim.tichu.games.tichu.mappers.CardMapper;
import com.icube.sim.tichu.games.tichu.mappers.PlayerMapper;
import com.icube.sim.tichu.rooms.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class TichuEventHandler {
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onSetRule(TichuSetRuleEvent event) {
        var message = TichuMessage.setRule(event.getRule());

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onGameStart(TichuStartEvent event) {
        var players = event.getPlayers();
        var playerMapper = new PlayerMapper();
        var playerDtos = Arrays.stream(players).map(playerMapper::toDto).toList();
        var message = TichuMessage.start(playerDtos);

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onFirstDraw(TichuFirstDrawEvent event) {
        var firstDraws = event.getFirstDraws();
        var cardMapper = new CardMapper();

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            var playerFirstDraws = cardMapper.toDtos(firstDraws.get(userId));
            var message = TichuMessage.initFirstDraws(playerFirstDraws);
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onLargeTichu(TichuLargeTichuEvent event) {
        var message = TichuMessage.largeTichu(event.getTichuDeclarations());

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onSecondDraw(TichuSecondDrawEvent event) {
        var hands = event.getHands();
        var cardMapper = new CardMapper();

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            var playerHand = cardMapper.toDtos(hands.get(userId));
            var message = TichuMessage.addSecondDraws(playerHand);
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onSmallTichu(TichuSmallTichuEvent event) {
        var message = TichuMessage.smallTichu(event.getPlayerId());

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            sendToUser(userId, message);
        }
    }

    @EventListener
    public void onExchange(TichuExchangeEvent event) {
        var cardMapper = new CardMapper();

        for (var userId : getRoomMemberIds(event.getRoomId())) {
            var message = TichuMessage.exchange(new ExchangeMessage(
                    cardMapper.toDto(event.getCardGaveLeftFrom(userId)),
                    cardMapper.toDto(event.getCardGaveMidFrom(userId)),
                    cardMapper.toDto(event.getCardGaveRightFrom(userId)),
                    cardMapper.toDto(event.getCardReceivedFromLeft(userId)),
                    cardMapper.toDto(event.getCardReceivedFromMid(userId)),
                    cardMapper.toDto(event.getCardReceivedFromRight(userId))
            ));
            sendToUser(userId, message);
        }
    }

    private Set<Long> getRoomMemberIds(String roomId) {
        var room = roomRepository.findById(roomId).orElseThrow();
        return room.getMembers().keySet();
    }

    private void sendToUser(Long userId, TichuMessage message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/game/tichu", message);
    }
}
