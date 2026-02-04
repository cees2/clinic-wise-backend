package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank
        String name,
        @NotBlank
        String surname,
        @NotNull
        Gender gender,
        @NotNull
        LocalDate dateOfBirth,
        @NotBlank
        String documentId,
        @NotBlank
        String address,
        @NotBlank
        String nationality,
        @NotBlank
        String phoneNumber,
        @NotNull
        LocalDate startDate
) {
}
