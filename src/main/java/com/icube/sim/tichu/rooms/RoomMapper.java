package com.icube.sim.tichu.rooms;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "memberCount", expression = "java(room.getMembers().size())")
    RoomOpaqueDto toOpaqueDto(Room room);

    RoomDto toDto(Room room);
}
