package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public record PatientResponse(
        Integer id,
        String name,
        String surname,
        Gender gender,
        LocalDate dateOfBirth,
        String documentId,
        String address,
        String nationality,
        String phoneNumber,
        LocalDate startDate
) {
}
