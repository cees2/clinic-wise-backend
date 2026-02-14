package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank
        String firstname,
        @NotBlank
        String lastname,
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
        LocalDate startDate,
        @NotBlank
        String email,
        @NotNull
        PatientSubscriptionPlan
        patientSubscriptionPlan,
        @NotNull
        Boolean enabled,
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
