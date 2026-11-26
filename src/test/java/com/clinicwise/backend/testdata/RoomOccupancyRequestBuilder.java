package com.clinicwise.backend.testdata;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;

import java.time.LocalDateTime;

public class RoomOccupancyRequestBuilder {
    private Integer roomId = 1;
    private LocalDateTime startTime = LocalDateTime.now().plusDays(3);
    private LocalDateTime endTime = LocalDateTime.now().plusDays(3).plusMinutes(30);
    private Integer employeeId = 1;

    public static RoomOccupancyRequestBuilder aRoomOccupancyRequestBuilder() {return new RoomOccupancyRequestBuilder();}

    public RoomOccupancyRequestBuilder withRoomId(Integer roomId){this.roomId = roomId; return this;}
    public RoomOccupancyRequestBuilder withStartTime(LocalDateTime startTime){this.startTime = startTime; return this;}
    public RoomOccupancyRequestBuilder withEndTime(LocalDateTime endTime){this.endTime = endTime; return this;}
    public RoomOccupancyRequestBuilder withEmployeeId(Integer employeeId){this.employeeId = employeeId; return this;}

    public CreateRoomOccupancyRequest build(){return new CreateRoomOccupancyRequest(roomId, startTime, endTime, employeeId);}
}
