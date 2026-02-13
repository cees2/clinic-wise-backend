package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public record EmployeeResponse(
        Integer id,
        LocalDate startDate,
        String name,
        String surname,
        Gender gender,
        LocalDate dateOfBirth,
        String nationality,
        String address,
        String phoneNumber,
        String email,
        EmployeeRole role,
        String documentId
) {
}
