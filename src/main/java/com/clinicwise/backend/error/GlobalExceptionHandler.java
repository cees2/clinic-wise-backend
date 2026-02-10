package com.clinicwise.backend.error;

import com.clinicwise.backend.exceptions.EmployeeWithProvidedDataExists;
import com.clinicwise.backend.exceptions.PatientWithProvidedDataExists;
import com.clinicwise.backend.exceptions.RoomOccupancyOverlapping;
import com.clinicwise.backend.exceptions.RoomWithNameExists;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> validationDataIntegrityHandler(DataIntegrityViolationException exception){
        Throwable error = exception.getMostSpecificCause();
        String message = "Invalid data provided";

        if(error instanceof SQLException SQLException){
            int errorCode = SQLException.getErrorCode();

            if(errorCode == 1062){
                message = "Resource already exists";
            } else if(errorCode == 1406){
                message = "Data too long";
            }
        }

        ApiError apiError = new ApiError(ErrorCode.INTEGRITY_ERROR, message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validationGlobalHandler(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> {
                            String field = fieldError.getField();
                            String value = fieldError.getDefaultMessage();

                            return new ValidationError(field, value);
                        }
                )
                .toList();
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, "Incorrect input provided", validationErrors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(EmployeeWithProvidedDataExists.class)
    public ResponseEntity<ApiError> employeeWithDocumentIDExistsHandler(EmployeeWithProvidedDataExists exception) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundExceptionHandler(EntityNotFoundException exception) {
        ApiError error = new ApiError(ErrorCode.ENTITY_NOT_FOUND, exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PatientWithProvidedDataExists.class)
    public ResponseEntity<ApiError> patientWithDataExistsHandler(PatientWithProvidedDataExists exception) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(RoomWithNameExists.class)
    public ResponseEntity<ApiError> patientWithDataExistsHandler(RoomWithNameExists exception) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(RoomOccupancyOverlapping.class)
    public ResponseEntity<ApiError> roomOccupancyOverlappingHandler(RoomOccupancyOverlapping exception) {
        ApiError error = new ApiError(ErrorCode.VALIDATION_ERROR, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> runtimeExceptionHandler(RuntimeException exception){
        ApiError error = new ApiError(ErrorCode.INTERNAL_ERROR, "Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
