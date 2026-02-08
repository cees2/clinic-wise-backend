package com.clinicwise.backend.service.dto;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;

public record RoomEmployee(
        Room room,
        Employee employee
) {
}
