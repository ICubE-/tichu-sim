package com.icube.sim.tichu.games.tichu.mappers;

import com.icube.sim.tichu.games.tichu.Player;
import com.icube.sim.tichu.games.tichu.dtos.PlayerDto;

public class PlayerMapper {
    public PlayerDto toDto(Player player) {
        return new PlayerDto(player.getId(), player.getName(), player.getTeam());
    }
}
