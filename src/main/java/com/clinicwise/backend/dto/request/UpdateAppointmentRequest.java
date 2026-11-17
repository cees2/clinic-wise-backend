package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateAppointmentRequest(
        @Min(5)
        @Max(90)
        Integer duration,
        @FutureOrPresent
        LocalDateTime startDate,
        AppointmentStatus status,
        String additionalNote,
        Integer patientId,
        Integer employeeId
) {
}
