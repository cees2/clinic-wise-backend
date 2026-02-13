package com.clinicwise.backend.dto.request;

public record CreateUserRequest(
        String username,
        String password,
        String confirmPassword
) {
}
