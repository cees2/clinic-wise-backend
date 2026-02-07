package com.clinicwise.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record CreateRoomRequest(
        @NotEmpty
        String name
) {
}
