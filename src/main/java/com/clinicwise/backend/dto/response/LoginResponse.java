package com.clinicwise.backend.dto.response;

public record LoginResponse(
        String token,
        UserResponse user
) {
}
