package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateRoomRequest;
import com.clinicwise.backend.dto.request.UpdateRoomRequest;
import com.clinicwise.backend.dto.response.RoomResponse;
import com.clinicwise.backend.entity.Room;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RoomMapper {
    public Room updateRoomFromRequest(UpdateRoomRequest updateRoomRequest, Room room){
        String name = updateRoomRequest.name();

        if(name != null) room.setName(name);

        return room;
    }

    public Room createRoomFromRequest(CreateRoomRequest createRoomRequest){
        Room room = new Room();
        room.setName(createRoomRequest.name());
        room.setCreatedAt(LocalDateTime.now());

        return room;
    }

    public static RoomResponse toResponse(Room room) {
        return new RoomResponse(room.getId(), room.getName());
    }
}
