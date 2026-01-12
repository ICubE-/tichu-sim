package com.icube.sim.tichu.rooms;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Room {
    private final String id;
    private final String name;
    private final List<Member> members;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(Long memberId) {
        members.removeIf(m -> m.id().equals(memberId));
    }

    public boolean containsMember(Long memberId) {
        return members.stream().anyMatch(m -> m.id().equals(memberId));
    }
}
