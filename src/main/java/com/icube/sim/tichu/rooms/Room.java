package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.games.tichu.TichuGame;
import com.icube.sim.tichu.games.tichu.exceptions.GameHasAlreadyStartedException;
import com.icube.sim.tichu.games.tichu.TichuGameRule;
import com.icube.sim.tichu.games.tichu.exceptions.GameNotFoundException;
import lombok.Getter;
import lombok.Locked;

import java.util.HashMap;
import java.util.Map;

public class Room {
    @Getter
    private final String id;
    @Getter
    private final String name;
    private final Map<Long, Member> members;
    @Getter
    private final TichuGameRule gameRule;
    private TichuGame game;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new HashMap<>();
        this.gameRule = new TichuGameRule();
        this.game = null;
    }

    @Locked.Read
    public Map<Long, Member> getMembers() {
        return Map.copyOf(members);
    }

    @Locked.Write
    public void addMember(Member member) {
        assert !members.containsKey(member.getId());
        if (members.size() == 4) {
            throw new TooManyMembersException();
        }
        if (hasGameStarted()) {
            throw new GameHasAlreadyStartedException();
        }

        members.put(member.getId(), member);
        member.setRoom(this);
    }

    @Locked.Write
    public void removeMember(Long memberId) {
        if (hasGameStarted()) {
            throw new GameHasAlreadyStartedException();
        }

        var member = members.remove(memberId);
        if (member != null) {
            member.setRoom(null);
        }
    }

    @Locked.Read
    public boolean containsMember(Long memberId) {
        return members.containsKey(memberId);
    }

    @Locked.Write
    public void setGameRule(TichuGameRule gameRule) {
        this.gameRule.set(gameRule);
    }

    @Locked.Read
    public boolean hasGameStarted() {
        return game != null && game.isPlaying();
    }

    @Locked.Write
    public void startGame() {
        if (members.size() != 4) {
            throw new InvalidMemberCountException();
        }
        if (hasGameStarted()) {
            throw new GameHasAlreadyStartedException();
        }

        gameRule.setMutable(false);
        game = new TichuGame(gameRule, members);
    }

    @Locked.Read
    public TichuGame getGame() {
        if (game == null) {
            throw new GameNotFoundException();
        }

        return game;
    }
}
