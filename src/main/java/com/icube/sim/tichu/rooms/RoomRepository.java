package com.icube.sim.tichu.rooms;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomRepository {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public void save(Room room) {
        rooms.put(room.getId(), room);
    }

    public Optional<Room> findById(String id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public boolean existsById(String id) {
        return rooms.containsKey(id);
    }

    public void deleteById(String id) {
        rooms.remove(id);
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
}
