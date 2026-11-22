package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoomOccupancyValidator extends DomainValidator<RoomOccupancy> {
    private final RoomOccupancyRepository roomOccupancyRepository;

    public RoomOccupancyValidator(RoomOccupancyRepository roomOccupancyRepository, EntityManager entityManager) {
        super(entityManager);
        this.roomOccupancyRepository = roomOccupancyRepository;
    }

    @Override
    protected List<ValidationError> customValidate(RoomOccupancy roomOccupancy) {
        List<ValidationError> validationErrors = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        if(roomOccupancy.getStartTime().isBefore(now)){
            validationErrors.add(new ValidationError("OCCUPANCY_START_IN_PAST", "Start time cannot be in the past", List.of("start_time"), null));
        }
        if(roomOccupancy.getEndTime().isBefore(now)){
            validationErrors.add(new ValidationError("OCCUPANCY_END_IN_PAST", "End time cannot be in the past", List.of("end_time"), null));
        }
        if(!roomOccupancy.getStartTime().isBefore(roomOccupancy.getEndTime())){
            validationErrors.add(new ValidationError("OCCUPANCY_TIME", "Start time must be before end time", List.of("start_time", "end_time"), null));
        }

        return validationErrors;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(RoomOccupancy roomOccupancy) {
        List<ValidationError> validationErrors = new ArrayList<>();
        List<RoomOccupancy> conflicting = roomOccupancyRepository.findAll(RoomOccupancySpecification.overlappingDatesAndRoomOrEmployee(roomOccupancy));

        if(conflicting.isEmpty()) return validationErrors;

        List<Integer> conflictingEmployees = conflicting.stream().filter(c -> c.getEmployee().getId().equals(roomOccupancy.getEmployee().getId())).map(RoomOccupancy::getId).toList();
        List<Integer> conflictingRooms = conflicting.stream().filter(c -> c.getRoom().getId().equals(roomOccupancy.getRoom().getId())).map(RoomOccupancy::getId).toList();

        if(!conflictingEmployees.isEmpty()){
            validationErrors.add(new ValidationError("OCCUPANCY_EMPLOYEE", "Employee is already occupied", List.of("employee_id"), Map.of("conflicting_room_occupancy_ids", conflictingEmployees)));
        }

        if(!conflictingRooms.isEmpty()){
            validationErrors.add(new ValidationError("OCCUPANCY_ROOM", "Room is already occupied", List.of("room_id"), Map.of("conflicting_room_occupancy_ids", conflictingRooms)));
        }

        return validationErrors;
    }
}
