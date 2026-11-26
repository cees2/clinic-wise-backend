package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.dto.request.UpdateRoomOccupancyRequest;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.mapper.RoomOccupancyMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import com.clinicwise.backend.repository.RoomRepository;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.RoomBuilder;
import com.clinicwise.backend.testdata.RoomOccupancyBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomOccupancyServiceTest {
    @InjectMocks
    private RoomOccupancyService roomOccupancyService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoomOccupancyRepository roomOccupancyRepository;
    @Mock
    private RoomOccupancyMapper roomOccupancyMapper;
    private Employee mockEmployee;
    private Room mockRoom;
    private CreateRoomOccupancyRequest mockCreateRoomOccupancyRequest;
    private UpdateRoomOccupancyRequest updateRoomOccupancyRequest;
    private RoomOccupancy mockRoomOccupancy;
    private static final int ROOM_OCCUPANCY_ID = 1;

    @BeforeEach
    void setup() {
        mockEmployee = EmployeeBuilder.anEmployee().withId(ROOM_OCCUPANCY_ID).build();
        mockRoom = RoomBuilder.aRoom().build();
        mockCreateRoomOccupancyRequest = new CreateRoomOccupancyRequest(mockRoom.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(30), mockEmployee.getId());
        updateRoomOccupancyRequest = new UpdateRoomOccupancyRequest(mockRoom.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(30), mockEmployee.getId());
        mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy().withId(ROOM_OCCUPANCY_ID).withRoom(mockRoom).withStartTime(LocalDateTime.now().plusMinutes(5)).withEndTime(LocalDateTime.now().plusMinutes(30)).withEmployee(mockEmployee).build();
    }

    @Test
    @DisplayName("Should not create a room occupancy if room does not exist")
    void createRoomOccupancySuchRoomDoesNotExist() {
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomOccupancyService.createRoomOccupancy(mockCreateRoomOccupancyRequest));
        verify(roomOccupancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not create a room occupancy if employee does not exist")
    void createRoomOccupancySuchEmployeeDoesNotExist() {
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.of(mockRoom));
        when(employeeRepository.findById(mockEmployee.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomOccupancyService.createRoomOccupancy(mockCreateRoomOccupancyRequest));
        verify(roomOccupancyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not update a room occupancy if room does not exist")
    void updateRoomOccupancySuchRoomDoesNotExist() {
        when(roomOccupancyRepository.findById(ROOM_OCCUPANCY_ID)).thenReturn(Optional.of(mockRoomOccupancy));
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomOccupancyService.updateRoomOccupancy(ROOM_OCCUPANCY_ID, updateRoomOccupancyRequest));
        verify(roomOccupancyMapper, never()).updateRoomOccupancyFromRequest(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should not update a room occupancy if employee does not exist")
    void updateRoomOccupancySuchEmployeeDoesNotExist() {
        when(roomOccupancyRepository.findById(ROOM_OCCUPANCY_ID)).thenReturn(Optional.of(mockRoomOccupancy));
        when(roomRepository.findById(mockRoom.getId())).thenReturn(Optional.of(mockRoom));
        when(employeeRepository.findById(mockEmployee.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomOccupancyService.updateRoomOccupancy(ROOM_OCCUPANCY_ID, updateRoomOccupancyRequest));
        verify(roomOccupancyMapper, never()).updateRoomOccupancyFromRequest(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should not update a room occupancy if room occupancy does not exist")
    void updateRoomOccupancySuchRoomOccupancyDoesNotExist() {
        when(roomOccupancyRepository.findById(ROOM_OCCUPANCY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomOccupancyService.updateRoomOccupancy(ROOM_OCCUPANCY_ID, updateRoomOccupancyRequest));
        verify(roomOccupancyMapper, never()).updateRoomOccupancyFromRequest(any(), any(), any(), any());
    }
}