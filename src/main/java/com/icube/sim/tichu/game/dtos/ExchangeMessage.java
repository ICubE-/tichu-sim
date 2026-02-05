package com.icube.sim.tichu.game.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExchangeMessage {
    private CardDto left;
    private CardDto mid;
    private CardDto right;
}
