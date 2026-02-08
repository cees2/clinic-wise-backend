package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.dto.response.RoomOccupancyResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RoomOccupancyMapper {
    public RoomOccupancy createRoomOccupancyFromRequest(CreateRoomOccupancyRequest createRoomOccupancyRequest, Room room, Employee employee) {
        RoomOccupancy roomOccupancy = new RoomOccupancy();

        roomOccupancy.setRoom(room);
        roomOccupancy.setEmployee(employee);
        roomOccupancy.setStartTime(createRoomOccupancyRequest.startTime());
        roomOccupancy.setEndTime(createRoomOccupancyRequest.endTime());
        roomOccupancy.setCreatedAt(LocalDateTime.now());

        return roomOccupancy;
    }

    public RoomOccupancy updateRoomOccupancyFromRequest(UpdateRoomOccupancyRequest updateRoomOccupancyRequest, RoomOccupancy roomOccupancy, Room room, Employee employee) {
        LocalDateTime startTime = updateRoomOccupancyRequest.startTime();
        LocalDateTime endTime = updateRoomOccupancyRequest.endTime();

        if (room != null) roomOccupancy.setRoom(room);
        if (employee != null) roomOccupancy.setEmployee(employee);
        if (startTime != null) roomOccupancy.setStartTime(startTime);
        if (endTime != null) roomOccupancy.setEndTime(endTime);

        return roomOccupancy;
    }

    public static RoomOccupancyResponse toResponse(RoomOccupancy roomOccupancy) {
        return new RoomOccupancyResponse(
                roomOccupancy.getRoom(),
                roomOccupancy.getEmployee(),
                roomOccupancy.getStartTime(),
                roomOccupancy.getEndTime()
        );
    }

}
