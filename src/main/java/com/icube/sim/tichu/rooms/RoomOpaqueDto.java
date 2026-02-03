package com.icube.sim.tichu.rooms;

import lombok.Data;

@Data
public class RoomOpaqueDto {
    private String id;
    private String name;
    private int memberCount;
    private boolean hasGameStarted;
}
