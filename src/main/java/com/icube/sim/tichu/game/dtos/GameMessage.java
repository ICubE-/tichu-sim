package com.icube.sim.tichu.game.dtos;

import com.icube.sim.tichu.game.GameRule;
import lombok.Getter;

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
}
