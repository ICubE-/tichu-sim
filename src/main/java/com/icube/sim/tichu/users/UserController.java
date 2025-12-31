package com.icube.sim.tichu.users;

import com.icube.sim.tichu.ErrorDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto register(@Valid @RequestBody RegisterUserRequest request) {
        return userService.register(request);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<@NonNull ErrorDto> handleDuplicateUser() {
        return ResponseEntity.badRequest().body(new ErrorDto(
                "The email is already registered."
        ));
    }
}
