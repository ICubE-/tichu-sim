package com.icube.sim.tichu.games.tichu.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayTrickMessage {
    private Long playerId;
    private TrickDto trick;
    private Integer wish;
}
