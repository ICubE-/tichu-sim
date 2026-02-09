package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.games.tichu.GameRule;
import lombok.Data;

import java.util.List;

@Data
public class RoomDto {
    private String id;
    private String name;
    private List<MemberDto> members;
    private boolean hasGameStarted;
    private GameRule gameRule;
}
