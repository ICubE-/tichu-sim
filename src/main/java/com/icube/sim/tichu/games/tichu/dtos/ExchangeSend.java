package com.icube.sim.tichu.games.tichu.dtos;

import lombok.Data;

@Data
public class ExchangeSend {
    private CardDto left;
    private CardDto mid;
    private CardDto right;
}
