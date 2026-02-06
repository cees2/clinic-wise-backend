package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Integer id,
        LocalDateTime createdAt,
        Integer duration,
        LocalDateTime startDate,
        AppointmentStatus status,
        String additionalNote,
        Patient patient,
        Employee employee
) {
}
