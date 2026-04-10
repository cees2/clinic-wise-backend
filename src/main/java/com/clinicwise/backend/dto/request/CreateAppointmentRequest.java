package com.clinicwise.backend.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull
        @Min(5)
        @Max(90)
        Integer duration,
        @NotNull
        @FutureOrPresent
        LocalDateTime startDate,
        @NotBlank
        String additionalNote,
        @NotNull
        Integer patientId,
        @NotNull
        Integer employeeId
) {
}
