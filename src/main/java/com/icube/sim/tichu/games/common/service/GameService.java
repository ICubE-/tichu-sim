package com.icube.sim.tichu.games.common.service;

import com.icube.sim.tichu.games.common.domain.GameRule;

public interface GameService {
    void setRule(String roomId, GameRule gameRule);
    void start(String roomId);
}
