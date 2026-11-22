package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserConflictsFinder {
    private final UserRepository userRepository;

    public UserConflictsFinder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ValidationError> findConflicts(User user) {
        List<ValidationError> validationErrors = new ArrayList<>();
        List<User> conflictingUsers = userRepository.findAll(UserSpecification.findConflicting(user));

        boolean conflictingDocumentId = conflictingUsers.stream().anyMatch(conflictingUser -> Objects.equals(conflictingUser.getDocumentId(), user.getDocumentId()));
        boolean conflictingPhoneNumber = conflictingUsers.stream().anyMatch(conflictingUser -> Objects.equals(conflictingUser.getPhoneNumber(), user.getPhoneNumber()));
        boolean conflictingEmail = conflictingUsers.stream().anyMatch(conflictingUser -> Objects.equals(conflictingUser.getUsername(), user.getUsername()));

        if(conflictingDocumentId){
            validationErrors.add(new ValidationError("DOCUMENT_ID_EXISTS", "A user with document id " + user.getDocumentId() + " already exists", List.of("document_id"), null));
        }
        if(conflictingPhoneNumber){
            validationErrors.add(new ValidationError("PHONE_NUMBER_EXISTS", "A user with phone number " + user.getPhoneNumber() + " already exists", List.of("phone_number"), null));
        }
        if(conflictingEmail){
            validationErrors.add(new ValidationError("EMAIL_EXISTS", "A user with email " + user.getUsername() + " already exists", List.of("email"), null));
        }

        return validationErrors;
    }
}
