package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;

import java.time.LocalDateTime;

public record RoomOccupancyResponse(
        Room room,
        EmployeeResponse employee,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer id
) {
}
