package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.dto.response.RoomOccupancyResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.exceptions.RoomOccupancyOverlapping;
import com.clinicwise.backend.exceptions.StartDateAfterEndDateException;
import com.clinicwise.backend.mapper.RoomOccupancyMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import com.clinicwise.backend.repository.RoomRepository;
import com.clinicwise.backend.service.dto.RoomEmployee;
import com.clinicwise.backend.specification.RoomOccupancySpecification;
import jakarta.persistence.EntityNotFoundException;
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


    public RoomOccupancyService(RoomOccupancyRepository roomOccupancyRepository, RoomOccupancyMapper roomOccupancyMapper, RoomRepository roomRepository, EmployeeRepository employeeRepository) {
        this.roomOccupancyRepository = roomOccupancyRepository;
        this.roomOccupancyMapper = roomOccupancyMapper;
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<RoomOccupancyResponse> getAllRoomOccupancies() {
        return roomOccupancyRepository.findAll()
                .stream()
                .map(RoomOccupancyMapper::toResponse)
                .toList();
    }

    public RoomOccupancyResponse getRoomOccupancy(int roomOccupancyId) {
        RoomOccupancy roomOccupancy = roomOccupancyRepository.findById(roomOccupancyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room occupancy with ID: " + roomOccupancyId));

        return RoomOccupancyMapper.toResponse(roomOccupancy);
    }

    @Transactional
    public RoomOccupancyResponse createRoomOccupancy(CreateRoomOccupancyRequest createRoomOccupancyRequest) {
        RoomEmployee roomEmployee = checkIfRoomAndEmployeeExists(createRoomOccupancyRequest.roomId(), createRoomOccupancyRequest.employeeId());
        checkIfEndTimeIfAfterStartTime(createRoomOccupancyRequest.startTime(), createRoomOccupancyRequest.endTime());
        findOverlappingRoomOccupancies(
                createRoomOccupancyRequest.startTime(),
                createRoomOccupancyRequest.endTime(),
                createRoomOccupancyRequest.roomId(),
                createRoomOccupancyRequest.employeeId());

        RoomOccupancy roomOccupancy = roomOccupancyMapper
                .createRoomOccupancyFromRequest(
                        createRoomOccupancyRequest,
                        roomEmployee.room(),
                        roomEmployee.employee()
                );

        roomOccupancyRepository.save(roomOccupancy);

        return RoomOccupancyMapper.toResponse(roomOccupancy);
    }

    @Transactional
    public RoomOccupancyResponse updateRoomOccupancy(int roomOccupancyId, UpdateRoomOccupancyRequest updateRoomOccupancyRequest) {
        RoomOccupancy roomOccupancy = roomOccupancyRepository.findById(roomOccupancyId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a room occupancy with ID: " + roomOccupancyId));

        checkIfEndTimeIfAfterStartTime(updateRoomOccupancyRequest.startTime(), updateRoomOccupancyRequest.endTime(), roomOccupancy.getStartTime(), roomOccupancy.getEndTime());

        LocalDateTime requestStartTime = Optional.of(updateRoomOccupancyRequest.startTime()).orElse(roomOccupancy.getStartTime());
        LocalDateTime requestEndTime = Optional.of(updateRoomOccupancyRequest.endTime()).orElse(roomOccupancy.getEndTime());

        findOverlappingRoomOccupancies(requestStartTime, requestEndTime, updateRoomOccupancyRequest.roomId(), updateRoomOccupancyRequest.employeeId());

        RoomEmployee roomEmployee = checkIfRoomAndEmployeeExists(updateRoomOccupancyRequest.roomId(), updateRoomOccupancyRequest.employeeId());

        roomOccupancyMapper.updateRoomOccupancyFromRequest(updateRoomOccupancyRequest, roomOccupancy, roomEmployee.room(), roomEmployee.employee());

        return RoomOccupancyMapper.toResponse(roomOccupancy);
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

    private void checkIfEndTimeIfAfterStartTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new StartDateAfterEndDateException("room occupancy", startTime, endTime);
        }
    }

    private void checkIfEndTimeIfAfterStartTime(
            LocalDateTime requestStartTime,
            LocalDateTime requestEndTime,
            LocalDateTime startTimeDB,
            LocalDateTime endTimeDB) {
        LocalDateTime startTime = Optional.ofNullable(requestStartTime).orElse(startTimeDB);
        LocalDateTime endTime = Optional.ofNullable(requestEndTime).orElse(endTimeDB);

        if (startTime.isAfter(endTime)) {
            throw new StartDateAfterEndDateException("room occupancy", startTime, endTime);
        }
    }

    private void findOverlappingRoomOccupancies(
            LocalDateTime requestStartTime,
            LocalDateTime requestEndTime,
            Integer roomId,
            Integer employeeId
    ) {
        List<RoomOccupancy> overlappingRoomOccupancies = roomOccupancyRepository
                .findAll(
                        RoomOccupancySpecification.overlappingDatesAndRoomOrEmployee(
                                requestStartTime,
                                requestEndTime,
                                roomId,
                                employeeId
                        )
                );

        if (!overlappingRoomOccupancies.isEmpty()) {
            throw new RoomOccupancyOverlapping(
                    requestStartTime,
                    requestEndTime,
                    overlappingRoomOccupancies.getFirst().getStartTime(),
                    overlappingRoomOccupancies.getFirst().getEndTime()
            );
        }
    }
}
