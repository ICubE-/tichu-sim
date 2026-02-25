package com.icube.sim.tichu.games.tichu.dtos;

import com.icube.sim.tichu.games.tichu.tricks.TrickType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TrickDto {
    private TrickType type;
    private List<CardDto> cardDtos;
}
