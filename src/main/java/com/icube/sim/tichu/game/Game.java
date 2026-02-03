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
        var memberIds = new ArrayList<>(members.keySet());
        Collections.shuffle(memberIds);
        var teams = rule.getDeterminedTeams(memberIds);

        var reds = teams.entrySet().stream()
                .filter(entry -> entry.getValue().equals(Team.RED))
                .map(Map.Entry::getKey)
                .toList();
        var blues = memberIds.stream()
                .filter(id -> !reds.contains(id))
                .toList();

        assert reds.size() == 2 && blues.size() == 2;

        players[0] = new Player(members.get(reds.get(0)), Team.RED);
        players[1] = new Player(members.get(blues.get(0)), Team.BLUE);
        players[2] = new Player(members.get(reds.get(1)), Team.RED);
        players[3] = new Player(members.get(blues.get(1)), Team.BLUE);
    }
}
