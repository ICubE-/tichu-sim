package com.icube.sim.tichu.rooms;

import com.icube.sim.tichu.ErrorDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public Map<String, Room> getRooms() {
        return roomService.getRooms();
    }

    @PostMapping
    public void createRoom(@Valid @RequestBody CreateRoomRequest request) {
        roomService.createRoom(request);
    }

    @PostMapping("/{id}")
    public void enterRoom(@PathVariable(name = "id") String id) {
        roomService.enterRoom(id);
    }

    @DeleteMapping("/{id}")
    public void leaveRoom(@PathVariable(name = "id") String id) {
        roomService.leaveRoom(id);
    }

    @ExceptionHandler(MemberAlreadyInOneRoomException.class)
    public ResponseEntity<@NonNull ErrorDto> handleMemberAlreadyInOneRoom() {
        return ResponseEntity.badRequest().body(new ErrorDto(
                "User is already in one room."
        ));
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<@NonNull ErrorDto> handleRoomNotFound() {
        return ResponseEntity.badRequest().body(new ErrorDto(
                "Room not found."
        ));
    }
}
