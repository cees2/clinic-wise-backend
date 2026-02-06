package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateAppointmentRequest(
        Integer duration,
        LocalDateTime startDate,
        AppointmentStatus status,
        String additionalNote,
        Integer patientId,
        Integer employeeId
) {
}
