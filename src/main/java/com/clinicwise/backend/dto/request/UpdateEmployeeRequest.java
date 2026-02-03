package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import java.time.LocalDate;

public record UpdateEmployeeRequest(
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
        String documentID
) {
}
