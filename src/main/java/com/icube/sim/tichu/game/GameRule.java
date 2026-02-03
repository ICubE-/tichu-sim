package com.icube.sim.tichu.game;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GameRule {
    private boolean isMutable = true;
    private Map<Long, Team> teamAssignment = new HashMap<>();
    private int timeLimit = 30;

    public void set(GameRule other) {
        if (!this.isMutable) {
            throw new ImmutableGameRuleException();
        }

        this.teamAssignment = other.teamAssignment;
        this.timeLimit = other.timeLimit;
    }
}
