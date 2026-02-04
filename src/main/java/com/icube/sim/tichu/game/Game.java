package com.icube.sim.tichu.game;

import com.icube.sim.tichu.game.dtos.GameMessage;
import com.icube.sim.tichu.rooms.Member;
import lombok.Synchronized;

import java.util.*;

public class Game {
    private GameStatus status;
    private final GameRule rule;
    // Player order: { RED, BLUE, RED, BLUE }
    private final Player[] players;
    private final Queue<GameMessage> messages;
    // Score order: { RED, BLUE }
    private final int[] scores;
    private final List<Round> rounds;

    public Game(GameRule rule, Map<Long, Member> members) {
        assert !rule.isMutable();

        this.status = GameStatus.PLAYING;
        this.rule = rule;
        this.players = new Player[4];
        setPlayers(members);

        this.messages = new LinkedList<>();
        enqueueMessage(GameMessage.start(players));

        this.scores = new int[2];
        this.rounds = new ArrayList<>();
        this.rounds.add(new Round(this));
    }

    public boolean isPlaying() {
        return status.equals(GameStatus.PLAYING);
    }

    public Player getPlayer(int index) {
        return players[index];
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

    @Synchronized("messages")
    public void enqueueMessage(GameMessage message) {
        messages.add(message);
    }

    @Synchronized("messages")
    public GameMessage dequeueMessage() {
        return messages.poll();
    }

    public Round getCurrentRound() {
        return rounds.get(rounds.size() - 1);
    }
}
