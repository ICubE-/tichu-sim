package com.icube.sim.tichu.game.dtos;

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
}
