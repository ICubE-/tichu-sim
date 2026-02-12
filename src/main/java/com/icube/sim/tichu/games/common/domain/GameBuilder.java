package com.icube.sim.tichu.games.common.domain;

import com.icube.sim.tichu.games.tichu.Tichu;
import com.icube.sim.tichu.games.tichu.TichuRule;
import com.icube.sim.tichu.rooms.Member;

import java.util.Map;

public class GameBuilder {
    public static Game build(GameName gameName, GameRule gameRule, Map<Long, Member> members) {
        return switch (gameName) {
            case TICHU -> new Tichu((TichuRule) gameRule, members);
        };
    }
}
