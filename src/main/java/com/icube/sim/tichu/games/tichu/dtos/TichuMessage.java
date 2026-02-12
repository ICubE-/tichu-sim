package com.icube.sim.tichu.games.tichu.dtos;

import com.icube.sim.tichu.games.tichu.Player;
import com.icube.sim.tichu.games.tichu.TichuDeclaration;
import com.icube.sim.tichu.games.tichu.TichuRule;
import com.icube.sim.tichu.games.tichu.cards.Card;
import com.icube.sim.tichu.games.tichu.mappers.CardMapper;
import com.icube.sim.tichu.games.tichu.mappers.PlayerMapper;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class TichuMessage {
    private final TichuMessageType type;
    private final Long targetUserId;
    private final Object data;

    private TichuMessage(TichuMessageType type, Long targetUserId, Object data) {
        this.type = type;
        this.targetUserId = targetUserId;
        this.data = data;
    }

    public static TichuMessage setRule(TichuRule rule) {
        return new TichuMessage(TichuMessageType.SET_RULE, null, rule);
    }

    public static TichuMessage start(Player[] players) {
        var playerMapper = new PlayerMapper();
        var playerDtos = Arrays.stream(players).map(playerMapper::toDto).toArray(PlayerDto[]::new);
        return new TichuMessage(TichuMessageType.START, null, playerDtos);
    }

    public static TichuMessage initFirstDraws(Long targetUserId, List<Card> firstDraws) {
        var cardMapper = new CardMapper();
        return new TichuMessage(TichuMessageType.INIT_FIRST_DRAWS, targetUserId, cardMapper.toDtos(firstDraws));
    }

    public static TichuMessage largeTichu(TichuDeclaration[] tichuDeclarations) {
        return new TichuMessage(TichuMessageType.LARGE_TICHU, null, tichuDeclarations.clone());
    }

    public static TichuMessage addSecondDraws(Long targetUserId, List<Card> secondDraws) {
        var cardMapper = new CardMapper();
        return new TichuMessage(TichuMessageType.ADD_SECOND_DRAWS, targetUserId, cardMapper.toDtos(secondDraws));
    }

    public static TichuMessage smallTichu(Player player) {
        return new TichuMessage(TichuMessageType.SMALL_TICHU, null, player.getId());
    }

    public static TichuMessage exchange(Long targetUserId, ExchangeMessage exchangeMessage) {
        return new TichuMessage(TichuMessageType.EXCHANGE, targetUserId, exchangeMessage);
    }
}
