package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.common.list.BaseFilter;
import com.clinicwise.backend.dto.request.CreateRoomRequest;
import com.clinicwise.backend.dto.request.UpdateRoomRequest;
import com.clinicwise.backend.dto.response.RoomResponse;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.exceptions.RoomWithNameExists;
import com.clinicwise.backend.mapper.RoomMapper;
import com.clinicwise.backend.repository.RoomRepository;
import com.clinicwise.backend.specification.RoomSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private RoomRepository roomRepository;
    private RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    public ListResponse<RoomResponse> getAllRooms(BaseFilter baseFilter) {
        Pageable pageable = PageRequest.of(baseFilter.getPage(), baseFilter.getSize() + 1);
        Page<Room> rooms = roomRepository.findAll(pageable);
        List<RoomResponse> roomResponses = roomRepository.findAll()
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
        boolean hasNext = rooms.getSize() > baseFilter.getSize();

        return ListResponse.toResponse(roomResponses, hasNext);
    }

    public ApiResponse<RoomResponse> getRoom(int roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Could not find a room with ID: " + roomId));

        return ApiResponse.toResponse(RoomMapper.toResponse(room));
    }

    @Transactional
    public ApiResponse<RoomResponse> createRoom(CreateRoomRequest createRoomRequest) {
        checkIfRoomWithMatchingNameExists(createRoomRequest.name());
        Room roomToBeCreated = roomMapper.createRoomFromRequest(createRoomRequest);

        roomRepository.save(roomToBeCreated);

        return ApiResponse.toResponse(RoomMapper.toResponse(roomToBeCreated));
    }

    @Transactional
    public ApiResponse<RoomResponse> updateRoom(int roomId, UpdateRoomRequest updateRoomRequest) {
        checkIfRoomWithMatchingNameExists(updateRoomRequest.name());
        Room roomToBeUpdated = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room with ID: " + roomId));

        roomMapper.updateRoomFromRequest(updateRoomRequest, roomToBeUpdated);

        return ApiResponse.toResponse(RoomMapper.toResponse(roomToBeUpdated));
    }

    @Transactional
    public void deleteRoom(int roomId) {
        Room roomToBeDeleted = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room with ID: " + roomId));

        roomRepository.delete(roomToBeDeleted);
    }

    public void checkIfRoomWithMatchingNameExists(String name) {
        if (name == null) return;

        Optional<Room> room = roomRepository.findOne(RoomSpecifications.hasName(name));

        if (room.isPresent()) {
            throw new RoomWithNameExists(name);
        }
    }
}
