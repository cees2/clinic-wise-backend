package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public record UserResponse(
        String username,
        String firstname,
        String lastname,
        Gender gender,
        String address,
        LocalDate dateOfBirth,
        String documentId,
        String email,
        boolean enabled,
        String nationality,
        String phoneNumber
) {
}
