package com.icube.sim.tichu.rooms;

import lombok.Getter;

@Getter
public class MembersMessage {
    private final MembersMessageType type;
    private final Long id;
    private final String name;

    private MembersMessage(MembersMessageType type, Long id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public static MembersMessage enter(Long id, String name) {
        return new MembersMessage(MembersMessageType.ENTER, id, name);
    }

    public static MembersMessage leave(Long id, String name) {
        return new MembersMessage(MembersMessageType.LEAVE, id, name);
    }

    enum MembersMessageType {
        ENTER,
        LEAVE,
    }
}
