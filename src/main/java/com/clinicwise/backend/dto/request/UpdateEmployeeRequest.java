package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public record UpdateEmployeeRequest(
        LocalDate startDate,
        String firstname,
        String lastname,
        Gender gender,
        LocalDate dateOfBirth,
        String nationality,
        String address,
        String phoneNumber,
        String email,
        EmployeeRole role,
        String documentId,
        String username,
        String password,
        Boolean enabled
) {
}
