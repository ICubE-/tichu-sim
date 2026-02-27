package com.icube.sim.tichu.games.tichu.dtos;

import com.icube.sim.tichu.games.tichu.TichuDeclaration;
import com.icube.sim.tichu.games.tichu.TichuRule;
import lombok.Getter;

import java.util.List;

@Getter
public class TichuMessage {
    private final TichuMessageType type;
    private final Object data;

    private TichuMessage(TichuMessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static TichuMessage setRule(TichuRule rule) {
        return new TichuMessage(TichuMessageType.SET_RULE, rule);
    }

    public static TichuMessage start(List<PlayerDto> players) {
        return new TichuMessage(TichuMessageType.START, players);
    }

    public static TichuMessage initFirstDraws(List<CardDto> firstDraws) {
        return new TichuMessage(TichuMessageType.INIT_FIRST_DRAWS, firstDraws);
    }

    public static TichuMessage largeTichu(TichuDeclaration[] tichuDeclarations) {
        return new TichuMessage(TichuMessageType.LARGE_TICHU, tichuDeclarations.clone());
    }

    public static TichuMessage addSecondDraws(List<CardDto> hand) {
        return new TichuMessage(TichuMessageType.ADD_SECOND_DRAWS, hand);
    }

    public static TichuMessage smallTichu(Long playerId) {
        return new TichuMessage(TichuMessageType.SMALL_TICHU, playerId);
    }

    public static TichuMessage exchange(ExchangeMessage exchangeMessage) {
        return new TichuMessage(TichuMessageType.EXCHANGE, exchangeMessage);
    }

    public static TichuMessage phaseStart(int firstPlayerIndex) {
        return new TichuMessage(TichuMessageType.PHASE_START, firstPlayerIndex);
    }

    public static TichuMessage playTrick(PlayTrickMessage playTrickMessage) {
        return new TichuMessage(TichuMessageType.PLAY_TRICK, playTrickMessage);
    }

    public static TichuMessage playBomb(PlayBombMessage playBombMessage) {
        return new TichuMessage(TichuMessageType.PLAY_BOMB, playBombMessage);
    }

    public static TichuMessage pass(Long playerId) {
        return new TichuMessage(TichuMessageType.PASS, playerId);
    }

    public static TichuMessage phaseEndWithDragon(int playerIndex) {
        return new TichuMessage(TichuMessageType.PHASE_END_WITH_DRAGON, playerIndex);
    }

    public static TichuMessage selectDragonReceiver(Long receiverId) {
        return new TichuMessage(TichuMessageType.SELECT_DRAGON_RECEIVER, receiverId);
    }

    public static TichuMessage roundEnd(List<int[]> scoresHistory) {
        return new TichuMessage(TichuMessageType.ROUND_END, scoresHistory);
    }

    public static TichuMessage end(List<int[]> scoresHistory) {
        return new TichuMessage(TichuMessageType.END, scoresHistory);
    }

    public static TichuMessage get(TichuDto tichuDto) {
        return new TichuMessage(TichuMessageType.GET, tichuDto);
    }
}
