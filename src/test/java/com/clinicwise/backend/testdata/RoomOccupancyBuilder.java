package com.clinicwise.backend.testdata;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;

import java.time.LocalDateTime;

public class RoomOccupancyBuilder {
    private Integer id = null;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Room room = RoomBuilder.aRoom().build();
    private LocalDateTime startTime = LocalDateTime.now().plusMinutes(30);
    private LocalDateTime endTime = LocalDateTime.now().plusMinutes(60);
    private Employee employee = EmployeeBuilder.anEmployee().build();

    public static RoomOccupancyBuilder aRoomOccupancy() {return new RoomOccupancyBuilder();}
    public RoomOccupancyBuilder withId(Integer id) {this.id = id; return this;}
    public RoomOccupancyBuilder withRoom(Room room) {this.room = room; return this;}
    public RoomOccupancyBuilder withStartTime(LocalDateTime startTime) {this.startTime = startTime; return this;}
    public RoomOccupancyBuilder withEndTime(LocalDateTime endTime) {this.endTime = endTime; return this;}
    public RoomOccupancyBuilder withEmployee(Employee employee) {this.employee = employee; return this;}

    public RoomOccupancy build() {
        RoomOccupancy roomOccupancy = new RoomOccupancy();
        roomOccupancy.setId(id);
        roomOccupancy.setCreatedAt(createdAt);
        roomOccupancy.setRoom(room);
        roomOccupancy.setStartTime(startTime);
        roomOccupancy.setEndTime(endTime);
        roomOccupancy.setEmployee(employee);
        return roomOccupancy;
    }
}
