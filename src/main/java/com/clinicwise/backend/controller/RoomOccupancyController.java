package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.dto.response.RoomOccupancyResponse;
import com.clinicwise.backend.service.RoomOccupancyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room_occupancies")
public class RoomOccupancyController {
    private RoomOccupancyService roomOccupancyService;

    public RoomOccupancyController(RoomOccupancyService roomOccupancyService) {
        this.roomOccupancyService = roomOccupancyService;
    }

    @GetMapping
    public List<RoomOccupancyResponse> getAllRoomOccupancies() {
        return roomOccupancyService.getAllRoomOccupancies();
    }

    @GetMapping("/{roomOccupancyId}")
    public RoomOccupancyResponse getRoomOccupancy(@PathVariable int roomOccupancyId) {
        return roomOccupancyService.getRoomOccupancy(roomOccupancyId);
    }

    @PostMapping
    public RoomOccupancyResponse createRoomOccupancy(@Valid @RequestBody CreateRoomOccupancyRequest createRoomOccupancyRequest) {
        return roomOccupancyService.createRoomOccupancy(createRoomOccupancyRequest);
    }

    @PatchMapping("/{roomOccupancyId}")
    public RoomOccupancyResponse updateRoomOccupancy(@PathVariable int roomOccupancyId, @RequestBody UpdateRoomOccupancyRequest updateRoomOccupancyRequest) {
        return roomOccupancyService.updateRoomOccupancy(roomOccupancyId, updateRoomOccupancyRequest);
    }

    @DeleteMapping("/{roomOccupancyId}")
    public ResponseEntity<Void> deleteRoomOccupancy(@PathVariable int roomOccupancyId) {
        roomOccupancyService.deleteRoomOccupancy(roomOccupancyId);

        return ResponseEntity.noContent().build();
    }
}
