package com.clinicwise.backend.error;

import java.util.List;

enum ErrorCode{
    VALIDATION_ERROR
}

public record ApiError(ErrorCode code, String message, List<ValidationError> validationErrors) {
    public ApiError(ErrorCode code, String message){
        this(code, message, null);
    }
}
