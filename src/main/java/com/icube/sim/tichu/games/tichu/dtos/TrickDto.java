package com.icube.sim.tichu.games.tichu.dtos;

import com.icube.sim.tichu.games.tichu.tricks.TrickType;

import java.util.List;

public record TrickDto(int playerIndex, TrickType type, List<CardDto> cardDtos) {
}
