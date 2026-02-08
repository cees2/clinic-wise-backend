package com.clinicwise.backend.dto.request;

import java.time.LocalDateTime;

public record UpdateRoomOccupancyRequest(
        Integer roomId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer employeeId
) {
}
