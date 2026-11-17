package com.clinicwise.backend.error.specificationError;

import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.exceptions.DomainValidationException;

import java.util.Collections;
import java.util.List;

public abstract class DomainValidator<Entity> {
    protected List<ValidationError> customValidate(Entity entity) { return Collections.emptyList(); }
    protected abstract List<ValidationError> findConflictingEntities(Entity entity);

    private void throwValidationError(List<ValidationError> validationErrors){
        if(!validationErrors.isEmpty()){
            throw new DomainValidationException("Incorrect data provided", validationErrors);
        }
    }

    public void validate(Entity entity){
        List<ValidationError> validationErrors = customValidate(entity);

        throwValidationError(validationErrors);
        throwValidationError(findConflictingEntities(entity));
    }
}
