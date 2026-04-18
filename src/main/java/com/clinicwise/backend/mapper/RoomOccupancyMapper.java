package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.dto.response.RoomOccupancyResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.faker.CustomFaker;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                roomOccupancy.getEndTime(),
                roomOccupancy.getId()
        );
    }

    public static List<RoomOccupancy> generateFakeRoomOccupancies(List<Room> rooms, List<Employee> employees) {
        CustomFaker faker = new CustomFaker();
        List<RoomOccupancy> roomOccupancies = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            RoomOccupancy roomOccupancy = new RoomOccupancy();

            int randomRoomIndex = faker.random().nextInt(rooms.size());
            int randomEmployeeIndex = faker.random().nextInt(employees.size());
            Room randomRoom = rooms.get(randomRoomIndex);
            Employee randomEmployee = employees.get(randomEmployeeIndex);
            LocalDateTime randomStartDate = MapperUtils.randomFutureDate5Days();
            LocalDateTime randomEndDate = MapperUtils.randomFutureDate1DayFromDate(randomStartDate);

            roomOccupancy.setCreatedAt(LocalDateTime.now());
            roomOccupancy.setRoom(randomRoom);
            roomOccupancy.setEmployee(randomEmployee);
            roomOccupancy.setStartTime(randomStartDate);
            roomOccupancy.setEndTime(randomEndDate);

            roomOccupancies.add(roomOccupancy);
        }

        return roomOccupancies;
    }
}
