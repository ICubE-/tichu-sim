package com.icube.sim.tichu.rooms;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Room {
    private final String id;
    private final String name;
    private final Map<Long, Member> members;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new HashMap<>();
    }

    public void addMember(Member member) {
        assert !members.containsKey(member.getId());
        if (members.size() == 4) {
            throw new TooManyMembersException();
        }

        members.put(member.getId(), member);
        member.setRoom(this);
    }

    public void removeMember(Long memberId) {
        var member = members.remove(memberId);
        if (member != null) {
            member.setRoom(null);
        }
    }

    public boolean containsMember(Long memberId) {
        return members.containsKey(memberId);
    }
}
