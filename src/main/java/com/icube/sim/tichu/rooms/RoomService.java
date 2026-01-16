package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
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
    private final RoomRepository roomRepository;
    private final MemberIdRepository memberIdRepository;
    private final RoomMapper roomMapper;

    public synchronized List<RoomOpaqueDto> getRooms() {
        return roomRepository.findAll().stream().map(roomMapper::toOpaqueDto).toList();
    }

    public synchronized CreateRoomResponse createRoom(CreateRoomRequest request) {
        var user = authService.getCurrentUser();
        if (memberIdRepository.exists(user.getId())) {
            throw new MemberAlreadyInOneRoomException();
        }

        String id;
        do {
            id = generateRandomAlphabetString(ROOM_ID_LENGTH);
        } while (roomRepository.existsById(id));

        var room = new Room(id, request.getName());
        room.addMember(new Member(user.getId(), user.getName()));
        roomRepository.save(room);
        memberIdRepository.save(user.getId());

        return new CreateRoomResponse(id);
    }

    public synchronized RoomDto getRoom(String id) {
        var user = authService.getCurrentUser();
        var room = roomRepository.findById(id).orElseThrow(RoomNotFoundException::new);
        if (!room.containsMember(user.getId())) {
            throw new AccessDeniedException("Not a member of this room.");
        }

        return roomMapper.toDto(room);
    }

    public synchronized void enterRoom(String id) {
        var user = authService.getCurrentUser();
        if (memberIdRepository.exists(user.getId())) {
            throw new MemberAlreadyInOneRoomException();
        }

        var room = roomRepository.findById(id).orElseThrow(RoomNotFoundException::new);
        room.addMember(new Member(user.getId(), user.getName()));
        memberIdRepository.save(user.getId());
    }

    public synchronized void leaveRoom(String id) {
        var user = authService.getCurrentUser();
        var room = roomRepository.findById(id).orElseThrow(RoomNotFoundException::new);
        room.removeMember(user.getId());
        memberIdRepository.delete(user.getId());

        if (room.getMembers().isEmpty()) {
            roomRepository.deleteById(id);
        }
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
