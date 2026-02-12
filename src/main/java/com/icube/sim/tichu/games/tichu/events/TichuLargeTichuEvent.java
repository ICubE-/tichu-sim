package com.icube.sim.tichu.games.tichu.events;

import com.icube.sim.tichu.games.tichu.TichuDeclaration;
import lombok.Getter;

@Getter
public class TichuLargeTichuEvent extends TichuEvent {
    private final TichuDeclaration[] tichuDeclarations;

    public TichuLargeTichuEvent(TichuDeclaration[] tichuDeclarations) {
        this.tichuDeclarations = tichuDeclarations.clone();
    }
}
