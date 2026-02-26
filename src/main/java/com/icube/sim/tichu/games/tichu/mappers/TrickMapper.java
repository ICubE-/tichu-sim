package com.icube.sim.tichu.games.tichu.mappers;

import com.icube.sim.tichu.games.tichu.dtos.TrickDto;
import com.icube.sim.tichu.games.tichu.tricks.Trick;

public class TrickMapper {
    private final CardMapper cardMapper = new CardMapper();

    public TrickDto toDto(Trick trick) {
        return new TrickDto(trick.getType(), cardMapper.toDtos(trick.getCards()));
    }
}
