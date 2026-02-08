package com.clinicwise.backend.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateRoomOccupancyRequest(
        @NotNull
        Integer roomId,
        @NotNull
        @FutureOrPresent
        LocalDateTime startTime,
        @NotNull
        @FutureOrPresent
        LocalDateTime endTime,
        @NotNull
        Integer employeeId
) {
}
