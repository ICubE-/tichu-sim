package com.icube.sim.tichu.games.tichu.dtos;

import lombok.Data;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Data
public class TrickSend {
    private List<CardDto> cards;
    @Nullable
    private Integer wish;
}
