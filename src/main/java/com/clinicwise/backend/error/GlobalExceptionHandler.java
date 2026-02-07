package com.clinicwise.backend.error;

import com.clinicwise.backend.exceptions.EmployeeWithProvidedDataExists;
import com.clinicwise.backend.exceptions.PatientWithProvidedDataExists;
import com.clinicwise.backend.exceptions.RoomWithNameExists;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmployeeWithProvidedDataExists.class)
    public ResponseEntity<ApiError> employeeWithDocumentIDExistsHandler (EmployeeWithProvidedDataExists exception) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundExceptionHandler(EntityNotFoundException exception){
        ApiError error = new ApiError(ErrorCode.ENTITY_NOT_FOUND, exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PatientWithProvidedDataExists.class)
    public ResponseEntity<ApiError> patientWithDataExistsHandler(PatientWithProvidedDataExists exception){
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(RoomWithNameExists.class)
    public ResponseEntity<ApiError> patientWithDataExistsHandler(RoomWithNameExists exception){
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
}
