package com.icube.sim.tichu.game.dtos;

import com.icube.sim.tichu.game.GameRule;
import com.icube.sim.tichu.game.Player;
import com.icube.sim.tichu.game.TichuDeclaration;
import com.icube.sim.tichu.game.cards.Card;
import com.icube.sim.tichu.game.mappers.CardMapper;
import com.icube.sim.tichu.game.mappers.PlayerMapper;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class GameMessage {
    private final GameMessageType type;
    private final Long targetUserId;
    private final Object data;

    private GameMessage(GameMessageType type, Long targetUserId, Object data) {
        this.type = type;
        this.targetUserId = targetUserId;
        this.data = data;
    }

    public static GameMessage setRule(GameRule rule) {
        return new GameMessage(GameMessageType.SET_RULE, null, rule);
    }

    public static GameMessage start(Player[] players) {
        var playerMapper = new PlayerMapper();
        var playerDtos = Arrays.stream(players).map(playerMapper::toDto).toArray(PlayerDto[]::new);
        return new GameMessage(GameMessageType.START, null, playerDtos);
    }

    public static GameMessage initFirstDraws(Long targetUserId, List<Card> firstDraws) {
        var cardMapper = new CardMapper();
        return new GameMessage(GameMessageType.INIT_FIRST_DRAWS, targetUserId, cardMapper.toDtos(firstDraws));
    }

    public static GameMessage largeTichu(TichuDeclaration[] tichuDeclarations) {
        return new GameMessage(GameMessageType.LARGE_TICHU, null, tichuDeclarations.clone());
    }

    public static GameMessage addSecondDraws(Long targetUserId, List<Card> secondDraws) {
        var cardMapper = new CardMapper();
        return new GameMessage(GameMessageType.ADD_SECOND_DRAWS, targetUserId, cardMapper.toDtos(secondDraws));
    }
}
