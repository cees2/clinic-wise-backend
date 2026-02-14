package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.EmployeeRole;

import java.time.LocalDate;

public record EmployeeResponse(
        Integer id,
        LocalDate startDate,
        EmployeeRole role,
        UserResponse user
) {
}
