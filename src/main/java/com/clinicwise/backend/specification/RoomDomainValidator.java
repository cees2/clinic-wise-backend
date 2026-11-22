package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import com.clinicwise.backend.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomDomainValidator extends DomainValidator<Room> {
    private final RoomRepository roomRepository;

    public RoomDomainValidator(EntityManager entityManager, RoomRepository roomRepository) {
        super(entityManager);
        this.roomRepository = roomRepository;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(Room room) {
        List<Room> conflicting = roomRepository.findAll(RoomSpecifications.hasName(room));
        List<ValidationError> validationErrors = new ArrayList<>();

        if(!conflicting.isEmpty())
            validationErrors.add(new ValidationError("ROOM_EXISTS", "A room with specified name already exists", List.of("name"), null));

        return validationErrors;
    }
}
