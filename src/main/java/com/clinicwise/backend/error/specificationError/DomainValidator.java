package com.clinicwise.backend.error.specificationError;

import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.exceptions.DomainValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;

import java.util.Collections;
import java.util.List;

public abstract class DomainValidator<Entity> {
    private final EntityManager entityManager;

    public DomainValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected List<ValidationError> customValidate(Entity entity) { return Collections.emptyList(); }
    protected abstract List<ValidationError> findConflictingEntities(Entity entity);

    private void throwValidationError(List<ValidationError> validationErrors){
        if(!validationErrors.isEmpty()){
            throw new DomainValidationException("Incorrect data provided", validationErrors);
        }
    }

    public void validate(Entity entity){
        throwValidationError(customValidate(entity));
        throwValidationError(findConflictingWithoutFlush(entity));
    }

    private List<ValidationError> findConflictingWithoutFlush(Entity entity){
        FlushModeType previous = entityManager.getFlushMode();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        try{
            return findConflictingEntities(entity);
        } finally {
            entityManager.setFlushMode(previous);
        }
    }
}
