package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public record PersonalDetailsResponse(
        String firstname,
        String lastname,
        Gender gender,
        String address,
        LocalDate dateOfBirth,
        String documentId,
        Country nationality,
        String phoneNumber
) {
}
