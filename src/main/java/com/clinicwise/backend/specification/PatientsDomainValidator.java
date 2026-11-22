package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientsDomainValidator extends DomainValidator<Patient> {
    private final UserConflictsFinder userConflictsFinder;

    public PatientsDomainValidator(UserConflictsFinder userConflictsFinder, EntityManager entityManager) {
        super(entityManager);
        this.userConflictsFinder = userConflictsFinder;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(Patient patient) {
        return userConflictsFinder.findConflicts(patient.getUser());
    }
}
