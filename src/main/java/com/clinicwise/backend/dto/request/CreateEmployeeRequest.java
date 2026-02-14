package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotNull
        LocalDate startDate,
        @NotBlank
        String firstname,
        @NotBlank
        String lastname,
        @NotNull
        Gender gender,
        @NotNull
        LocalDate dateOfBirth,
        @NotBlank
        String nationality,
        @NotBlank
        String address,
        @NotBlank
        String phoneNumber,
        @NotBlank
        String email,
        @NotNull
        EmployeeRole role,
        @NotBlank
        String documentId,
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotNull
        Boolean enabled
) {
}
