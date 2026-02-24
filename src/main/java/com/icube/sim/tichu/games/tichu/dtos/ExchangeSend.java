package com.icube.sim.tichu.games.tichu.dtos;

import lombok.Data;
import org.jspecify.annotations.Nullable;

@Data
public class ExchangeSend {
    @Nullable
    private CardDto left;
    @Nullable
    private CardDto mid;
    @Nullable
    private CardDto right;
}
