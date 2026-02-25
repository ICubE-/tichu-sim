package com.icube.sim.tichu.games.tichu.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BombSend {
    private List<CardDto> cards;
}
