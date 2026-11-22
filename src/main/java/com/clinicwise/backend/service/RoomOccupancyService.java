package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.common.list.filter.RoomsOccupancyFilter;
import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.dto.response.RoomOccupancyResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.mapper.RoomOccupancyMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import com.clinicwise.backend.repository.RoomRepository;
import com.clinicwise.backend.service.dto.RoomEmployee;
import com.clinicwise.backend.specification.RoomOccupancySpecification;
import com.clinicwise.backend.specification.RoomOccupancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomOccupancyService {
    private RoomOccupancyRepository roomOccupancyRepository;
    private RoomOccupancyMapper roomOccupancyMapper;
    private RoomRepository roomRepository;
    private EmployeeRepository employeeRepository;
    private RoomOccupancyValidator roomOccupancyValidator;

    public RoomOccupancyService(RoomOccupancyRepository roomOccupancyRepository, RoomOccupancyMapper roomOccupancyMapper, RoomRepository roomRepository, EmployeeRepository employeeRepository, RoomOccupancyValidator roomOccupancyValidator) {
        this.roomOccupancyRepository = roomOccupancyRepository;
        this.roomOccupancyMapper = roomOccupancyMapper;
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
        this.roomOccupancyValidator = roomOccupancyValidator;
    }

    public PageResponse<RoomOccupancyResponse> getAllRoomOccupancies(RoomsOccupancyFilter roomsOccupancyFilter) {
        Pageable pageable = PageRequest.of(roomsOccupancyFilter.getPage(), roomsOccupancyFilter.getSize() + 1);
        Page<RoomOccupancy> appointments = roomOccupancyRepository.findAll(RoomOccupancySpecification.whereFilter(roomsOccupancyFilter), pageable);
        List<RoomOccupancyResponse> roomOccupancies = appointments.stream()
                .map(roomOccupancyMapper::toResponse)
                .toList();
        boolean hasNext = appointments.getNumberOfElements() == roomsOccupancyFilter.getSize();

        return PageResponse.toResponse(roomOccupancies, hasNext);
    }

    public ApiResponse<RoomOccupancyResponse> getRoomOccupancy(int roomOccupancyId) {
        RoomOccupancy roomOccupancy = roomOccupancyRepository.findById(roomOccupancyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room occupancy with ID: " + roomOccupancyId));

        return ApiResponse.toResponse(roomOccupancyMapper.toResponse(roomOccupancy));
    }

    @Transactional
    public ApiResponse<RoomOccupancyResponse> createRoomOccupancy(CreateRoomOccupancyRequest createRoomOccupancyRequest) {
        RoomEmployee roomEmployee = checkIfRoomAndEmployeeExists(createRoomOccupancyRequest.roomId(), createRoomOccupancyRequest.employeeId());
        RoomOccupancy roomOccupancy = roomOccupancyMapper
                .createRoomOccupancyFromRequest(
                        createRoomOccupancyRequest,
                        roomEmployee.room(),
                        roomEmployee.employee()
                );

        roomOccupancyValidator.validate(roomOccupancy);
        roomOccupancyRepository.save(roomOccupancy);

        return ApiResponse.toResponse(roomOccupancyMapper.toResponse(roomOccupancy));
    }

    @Transactional
    public ApiResponse<RoomOccupancyResponse> updateRoomOccupancy(int roomOccupancyId, UpdateRoomOccupancyRequest updateRoomOccupancyRequest) {
        RoomOccupancy roomOccupancy = roomOccupancyRepository.findById(roomOccupancyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room occupancy with ID: " + roomOccupancyId));
        RoomEmployee roomEmployee = checkIfRoomAndEmployeeExists(updateRoomOccupancyRequest.roomId(), updateRoomOccupancyRequest.employeeId());
        RoomOccupancy updated = roomOccupancyMapper.updateRoomOccupancyFromRequest(updateRoomOccupancyRequest, roomOccupancy, roomEmployee.room(), roomEmployee.employee());
        roomOccupancyValidator.validate(updated);

        return ApiResponse.toResponse(roomOccupancyMapper.toResponse(roomOccupancy));
    }

    @Transactional
    public void deleteRoomOccupancy(int roomOccupancyId) {
        RoomOccupancy roomOccupancy = roomOccupancyRepository.findById(roomOccupancyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room occupancy with ID: " + roomOccupancyId));

        roomOccupancyRepository.delete(roomOccupancy);
    }

    private RoomEmployee checkIfRoomAndEmployeeExists(Integer roomId, Integer employeeId) {
        Room room = Optional.ofNullable(roomId)
                .map((nullableRoomId) ->
                        roomRepository.findById(nullableRoomId)
                                .orElseThrow(() -> new EntityNotFoundException("Could not find a room with ID: " + roomId)))
                .orElse(null);
        Employee employee = Optional.ofNullable(employeeId)
                .map(nullableEmployeeId ->
                        employeeRepository.findById(nullableEmployeeId)
                                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with ID: " + employeeId)))
                .orElse(null);

        return new RoomEmployee(room, employee);
    }

    public ApiResponse<List<RoomOccupancyResponse>> generateRoomOccupancies() {
        List<Employee> employees = employeeRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<RoomOccupancy> roomOccupancies = roomOccupancyMapper.generateFakeRoomOccupancies(rooms, employees);

        roomOccupancyRepository.saveAll(roomOccupancies);

        return ApiResponse.toResponse(
                roomOccupancies.stream()
                        .map(roomOccupancyMapper::toResponse)
                        .toList()
        );
    }
}
