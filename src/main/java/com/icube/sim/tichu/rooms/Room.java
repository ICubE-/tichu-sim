package com.icube.sim.tichu.rooms;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Room {
    private final String id;
    private final String name;
    private final Set<Member> members;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new HashSet<>();
    }

    public void addMember(Member member) {
        assert members.stream().noneMatch(m -> m.id().equals(member.id()));
        if (members.size() == 4) {
            throw new TooManyMembersException();
        }

        members.add(member);
        member.setRoom(this);
    }

    public void removeMember(Long memberId) {
        members.removeIf(m -> m.id().equals(memberId));
    }

    public boolean containsMember(Long memberId) {
        return members.stream().anyMatch(m -> m.id().equals(memberId));
    }
}
