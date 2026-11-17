package com.clinicwise.backend.exceptions;

import com.clinicwise.backend.error.ValidationError;

import java.util.List;

public class DomainValidationException extends RuntimeException {
    private List<ValidationError> validationErrors;

    public DomainValidationException(String message) {
        super(message);
    }

    public DomainValidationException(String message, List<ValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}
