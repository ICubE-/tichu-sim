package com.icube.sim.tichu.games.tichu.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayBombMessage {
    private Long playerId;
    private TrickDto bomb;
}
