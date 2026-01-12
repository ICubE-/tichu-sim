package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class RoomService {
    @Value("${spring.rooms.id-length}")
    private int ROOM_ID_LENGTH;

    private final AuthService authService;
    private final Map<String, Room> rooms = new HashMap<>();
    private final Set<Long> memberIds = new HashSet<>();

    public synchronized Map<String, Room> getRooms() {
        return rooms;
    }

    public synchronized void createRoom(CreateRoomRequest request) {
        var user = authService.getCurrentUser();
        if (memberIds.contains(user.getId())) {
            throw new MemberAlreadyInOneRoomException();
        }

        String id;
        do {
            id = generateRandomAlphabetString(ROOM_ID_LENGTH);
        } while (rooms.containsKey(id));

        var room = new Room(id, request.getName());
        rooms.put(id, room);
        room.addMember(new Member(user.getId(), user.getName()));
        memberIds.add(user.getId());
    }

    public synchronized void enterRoom(String id) {
        var user = authService.getCurrentUser();
        if (memberIds.contains(user.getId())) {
            throw new MemberAlreadyInOneRoomException();
        }

        var room = rooms.get(id);
        if (room == null) {
            throw new RoomNotFoundException();
        }

        room.addMember(new Member(user.getId(), user.getName()));
        memberIds.add(user.getId());
    }

    public synchronized void leaveRoom(String id) {
        var user = authService.getCurrentUser();
        var room = rooms.get(id);
        if (room == null) {
            throw new RoomNotFoundException();
        }

        room.removeMember(user.getId());
        memberIds.remove(user.getId());
    }

    private static String generateRandomAlphabetString(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        var random = new Random();
        return IntStream.range(0, length)
                .map(i -> random.nextInt(CHARACTERS.length()))
                .mapToObj(CHARACTERS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
