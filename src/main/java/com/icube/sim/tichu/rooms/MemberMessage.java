package com.icube.sim.tichu.rooms;

import lombok.Getter;

@Getter
public class MemberMessage {
    private final MembersMessageType type;
    private final Long id;
    private final String name;

    private MemberMessage(MembersMessageType type, Long id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public static MemberMessage enter(Long id, String name) {
        return new MemberMessage(MembersMessageType.ENTER, id, name);
    }

    public static MemberMessage leave(Long id, String name) {
        return new MemberMessage(MembersMessageType.LEAVE, id, name);
    }

    enum MembersMessageType {
        ENTER,
        LEAVE,
    }
}
