package com.icube.sim.tichu.rooms;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "memberCount", expression = "java(room.getMembers().size())")
    RoomOpaqueDto toOpaqueDto(Room room);

    RoomDto toDto(Room room);

    // Helper method
    default List<MemberDto> membersMapToDtoList(Map<Long, Member> map) {
        if (map == null) {
            return null;
        }

        return map.values().stream()
                .map(member -> new MemberDto(member.getId(), member.getName()))
                .toList();
    }
}
