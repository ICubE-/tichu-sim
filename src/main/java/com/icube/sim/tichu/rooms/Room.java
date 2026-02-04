package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.game.Game;
import com.icube.sim.tichu.game.exceptions.GameHasAlreadyStartedException;
import com.icube.sim.tichu.game.GameRule;
import lombok.Getter;
import lombok.Locked;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Room {
    private final String id;
    private final String name;
    private final Map<Long, Member> members;
    private final GameRule gameRule;
    private Game game;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new HashMap<>();
        this.gameRule = new GameRule();
        this.game = null;
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
    public void setGameRule(GameRule gameRule) {
        gameRule.set(gameRule);
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
        game = new Game(gameRule, members);
    }
}
