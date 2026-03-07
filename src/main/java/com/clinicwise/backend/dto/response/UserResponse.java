package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

public record UserResponse(
        String username,
        String firstname,
        String lastname,
        Gender gender,
        String address,
        LocalDate dateOfBirth,
        String documentId,
        boolean enabled,
        String nationality,
        String phoneNumber,
        Set<String> authorities
) {
}
