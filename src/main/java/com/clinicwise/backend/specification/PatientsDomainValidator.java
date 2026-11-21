package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientsDomainValidator extends DomainValidator<Patient> {
    private final UserRepository userRepository;

    public PatientsDomainValidator(UserRepository userRepository, EntityManager entityManager) {
        super(entityManager);
        this.userRepository = userRepository;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(Patient patient) {
        User patientUser = patient.getUser();
        List<ValidationError> validationErrors = new ArrayList<>();
        List<User> conflictingUsers = userRepository.findAll(UserSpecification.findConflicting(patientUser));

        boolean patientsWithConflictingDocumentId = conflictingUsers.stream().anyMatch(conflictingUser -> conflictingUser.getDocumentId().equals(patientUser.getDocumentId()));
        boolean patientsWithConflictingPhoneNumber = conflictingUsers.stream().anyMatch(conflictingUser -> conflictingUser.getPhoneNumber().equals(patientUser.getPhoneNumber()));
        boolean patientsWithConflictingEmails = conflictingUsers.stream().anyMatch(conflictingUser -> conflictingUser.getUsername().equals(patientUser.getUsername()));

        if(patientsWithConflictingDocumentId){
            validationErrors.add(new ValidationError("DOCUMENT_ID_EXISTS", "A user with document id " + patientUser.getDocumentId() + " already exists", List.of("document_id"), null));
        }
        if(patientsWithConflictingPhoneNumber){
            validationErrors.add(new ValidationError("PHONE_NUMBER_EXISTS", "A user with phone number " + patientUser.getPhoneNumber() + " already exists", List.of("phone_number"), null));
        }
        if(patientsWithConflictingEmails){
            validationErrors.add(new ValidationError("EMAIL_EXISTS", "A user with email " + patientUser.getUsername() + " already exists", List.of("email"), null));
        }

        return validationErrors;
    }
}
