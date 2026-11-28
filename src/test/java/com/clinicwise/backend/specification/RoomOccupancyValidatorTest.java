package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.RoomBuilder;
import com.clinicwise.backend.testdata.RoomOccupancyBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomOccupancyValidatorTest {
    @Mock
    private RoomOccupancyRepository roomOccupancyRepository;
    @InjectMocks
    private RoomOccupancyValidator roomOccupancyValidator;

    @Test
    @DisplayName("Should validate a room occupancy")
    void customValidateOk() {
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withStartTime(LocalDateTime.now().plusHours(1))
                .withEndTime(LocalDateTime.now().plusHours(2))
                .build();
        List<ValidationError> validationErrors = roomOccupancyValidator.customValidate(mockRoomOccupancy);

        assertTrue(validationErrors.isEmpty());
    }

    @Test
    @DisplayName("Should return validation error - startTime is in the past")
    void customValidateStartTimeInThePast() {
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withStartTime(LocalDateTime.now().minusHours(1))
                .build();
        List<ValidationError> validationErrors = roomOccupancyValidator.customValidate(mockRoomOccupancy);
        ValidationError validationError = validationErrors.getFirst();

        assertEquals(1, validationErrors.size());
        assertEquals("OCCUPANCY_START_IN_PAST", validationError.code());
        assertEquals("Start time cannot be in the past", validationError.message());
        assertEquals(1, validationError.fields().size());
        assertEquals("start_time", validationError.fields().getFirst());
    }

    @Test
    @DisplayName("Should return validation error - endTime is in the past")
    void customValidateEndTimeInThePast() {
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withStartTime(LocalDateTime.now().minusHours(2))
                .withEndTime(LocalDateTime.now().minusHours(1))
                .build();
        List<ValidationError> validationErrors = roomOccupancyValidator.customValidate(mockRoomOccupancy);
        ValidationError validationError = validationErrors.stream()
                .filter(error -> "OCCUPANCY_END_IN_PAST".equals(error.code()))
                .findFirst()
                .orElseThrow();

        assertEquals(List.of("OCCUPANCY_START_IN_PAST", "OCCUPANCY_END_IN_PAST"),
                validationErrors.stream().map(ValidationError::code).toList());
        assertEquals("End time cannot be in the past", validationError.message());
        assertEquals(1, validationError.fields().size());
        assertEquals("end_time", validationError.fields().getFirst());
    }

    @Test
    @DisplayName("Should return validation error - startTime is after endTime")
    void customValidateStartTimeAfterEndTime() {
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withStartTime(LocalDateTime.now().plusHours(2))
                .withEndTime(LocalDateTime.now().plusHours(1))
                .build();
        List<ValidationError> validationErrors = roomOccupancyValidator.customValidate(mockRoomOccupancy);
        ValidationError validationError = validationErrors.getFirst();

        assertEquals(1, validationErrors.size());
        assertEquals("OCCUPANCY_TIME", validationError.code());
        assertEquals("Start time must be before end time", validationError.message());
        assertEquals(List.of("start_time", "end_time"), validationError.fields());
    }

    @Test
    @DisplayName("Should return validation error - startTime equals endTime")
    void customValidateStartTimeEqualsEndTime() {
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withStartTime(time)
                .withEndTime(time)
                .build();
        List<ValidationError> validationErrors = roomOccupancyValidator.customValidate(mockRoomOccupancy);
        ValidationError validationError = validationErrors.getFirst();

        assertEquals(1, validationErrors.size());
        assertEquals("OCCUPANCY_TIME", validationError.code());
        assertEquals("Start time must be before end time", validationError.message());
        assertEquals(List.of("start_time", "end_time"), validationError.fields());
    }

    @Test
    @DisplayName("Should return validation error - conflicting room occupancy employee")
    void findConflictingEntitiesStartTimeEqualsEndTime() {
        Employee conflictingEmployee = EmployeeBuilder.anEmployee().withId(2).build();
        Room room1 = RoomBuilder.aRoom().withId(1).build();
        Room room2 = RoomBuilder.aRoom().withId(2).build();
        RoomOccupancy conflictingRoomOccupancy = RoomOccupancyBuilder
                .aRoomOccupancy()
                .withId(2)
                .withStartTime(LocalDateTime.now().plusMinutes(30))
                .withEndTime(LocalDateTime.now().plusMinutes(90))
                .withEmployee(conflictingEmployee)
                .withRoom(room1)
                .build();
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withId(1)
                .withStartTime(LocalDateTime.now().plusHours(1))
                .withEndTime(LocalDateTime.now().plusHours(2))
                .withEmployee(conflictingEmployee)
                .withRoom(room2)
                .build();

        when(roomOccupancyRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(conflictingRoomOccupancy));

        List<ValidationError> validationErrors = roomOccupancyValidator.findConflictingEntities(mockRoomOccupancy);
        ValidationError validationError = validationErrors.getFirst();

        assertEquals(1, validationErrors.size());
        assertEquals("OCCUPANCY_EMPLOYEE", validationError.code());
        assertEquals("Employee is already occupied", validationError.message());
        assertEquals(List.of("employee_id"), validationError.fields());
        assertEquals(List.of(2), validationError.details().get("conflicting_room_occupancy_ids"));
    }

    @Test
    @DisplayName("Should return validation error - conflicting room occupancy room")
    void findConflictingEntitiesRoomOccupied() {
        Room conflictingRoom = RoomBuilder.aRoom().withId(1).build();
        Employee employee1 = EmployeeBuilder.anEmployee().withId(1).build();
        Employee employee2 = EmployeeBuilder.anEmployee().withId(2).build();
        RoomOccupancy conflictingRoomOccupancy = RoomOccupancyBuilder
                .aRoomOccupancy()
                .withId(2)
                .withStartTime(LocalDateTime.now().plusMinutes(30))
                .withEndTime(LocalDateTime.now().plusMinutes(90))
                .withEmployee(employee1)
                .withRoom(conflictingRoom)
                .build();
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy()
                .withId(1)
                .withStartTime(LocalDateTime.now().plusHours(1))
                .withEndTime(LocalDateTime.now().plusHours(2))
                .withEmployee(employee2)
                .withRoom(conflictingRoom)
                .build();

        when(roomOccupancyRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(conflictingRoomOccupancy));

        List<ValidationError> validationErrors = roomOccupancyValidator.findConflictingEntities(mockRoomOccupancy);
        ValidationError validationError = validationErrors.getFirst();

        assertEquals(1, validationErrors.size());
        assertEquals("OCCUPANCY_ROOM", validationError.code());
        assertEquals("Room is already occupied", validationError.message());
        assertEquals(List.of("room_id"), validationError.fields());
        assertEquals(List.of(2), validationError.details().get("conflicting_room_occupancy_ids"));
    }

    @Test
    @DisplayName("Should validate room occupancies")
    void findConflictingEntitiesOk() {
        when(roomOccupancyRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of());
        RoomOccupancy mockRoomOccupancy = RoomOccupancyBuilder.aRoomOccupancy().build();

        List<ValidationError> result = roomOccupancyValidator.findConflictingEntities(mockRoomOccupancy);

        assertTrue(result.isEmpty());
    }
}