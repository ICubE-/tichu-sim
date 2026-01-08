package com.icube.sim.tichu.rooms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotBlank(message = "Name is required.")
    @Size(max = 64, message = "Name must be less than 64 characters.")
    private String name;
}
