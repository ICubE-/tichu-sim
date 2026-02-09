package com.icube.sim.tichu.game.dtos;

import com.icube.sim.tichu.game.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerDto {
    private Long id;
    private String name;
    private Team team;
}
