package com.icube.sim.tichu.games.tichu.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExchangeMessage {
    private CardDto gaveToLeft;
    private CardDto gaveToMid;
    private CardDto gaveToRight;
    private CardDto receivedFromLeft;
    private CardDto receivedFromMid;
    private CardDto receivedFromRight;
}
