package com.icube.sim.tichu.game.mappers;

import com.icube.sim.tichu.game.Player;
import com.icube.sim.tichu.game.dtos.PlayerDto;

public class PlayerMapper {
    public PlayerDto toDto(Player player) {
        return new PlayerDto(player.getId(), player.getName(), player.getTeam());
    }
}
