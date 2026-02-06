package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.AppointmentStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull
        @Min(5)
        @Max(90)
        Integer duration,
        @NotNull
        @FutureOrPresent
        LocalDateTime startDate,
        @NotNull
        AppointmentStatus status,
        String additionalNote,
        @NotNull
        Integer patientId,
        @NotNull
        Integer employeeId
) {
}
