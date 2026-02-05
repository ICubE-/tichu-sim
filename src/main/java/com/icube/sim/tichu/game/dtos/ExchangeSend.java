package com.icube.sim.tichu.game.dtos;

import lombok.Data;

@Data
public class ExchangeSend {
    private CardDto left;
    private CardDto mid;
    private CardDto right;
}
