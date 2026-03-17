package com.icube.sim.tichu.games.tichu.mappers;

import com.icube.sim.tichu.games.tichu.dtos.TrickDto;
import com.icube.sim.tichu.games.tichu.tricks.Trick;

import java.util.List;

public class TrickMapper {
    private final CardMapper cardMapper = new CardMapper();

    public TrickDto toDto(Trick trick) {
        return new TrickDto(trick.getPlayerIndex(), trick.getType(), cardMapper.toDtos(trick.getCards()));
    }

    public List<TrickDto> toDtos(List<Trick> tricks) {
        return tricks.stream().map(this::toDto).toList();
    }
}
