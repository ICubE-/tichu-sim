package com.icube.sim.tichu.game;

import com.icube.sim.tichu.rooms.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Getter
public class Game {
    private GameStatus status;
    private final GameRule rule;
    // Player order: { RED, BLUE, RED, BLUE }
    private final Player[] players;
    // Score order: { RED, BLUE }
    private final int[] scores;

    public Game(GameRule rule, Map<Long, Member> members) {
        assert !rule.isMutable();

        this.status = GameStatus.PLAYING;
        this.rule = rule;
        this.players = new Player[4];
        setPlayers(members);
        this.scores = new int[2];
    }

    public boolean isPlaying() {
        return status.equals(GameStatus.PLAYING);
    }

    private void setPlayers(Map<Long, Member> members) {
    }
}
