package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.CreateRoomRequest;
import com.clinicwise.backend.dto.request.UpdateRoomRequest;
import com.clinicwise.backend.dto.response.RoomResponse;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable int roomId){
        return roomService.getRoom(roomId);
    }

    @PostMapping
    public RoomResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest){
        return roomService.createRoom(createRoomRequest);
    }

    @PatchMapping("/{roomId}")
    public RoomResponse updateRoom(@PathVariable int roomId, @RequestBody UpdateRoomRequest updateRoomRequest){
        return roomService.updateRoom(roomId, updateRoomRequest);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable int roomId){
        roomService.deleteRoom(roomId);

        return ResponseEntity.noContent().build();
    }
}
