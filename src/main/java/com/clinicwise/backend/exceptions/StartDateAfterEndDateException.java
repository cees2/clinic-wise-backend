package com.clinicwise.backend.exceptions;

import java.time.LocalDateTime;

public class StartDateAfterEndDateException extends RuntimeException {
    public StartDateAfterEndDateException(String entityName, LocalDateTime startDate, LocalDateTime endDate) {
        super("Provided dates for " + entityName + " are invalid. The date " + startDate + " is after the end date " + endDate);
    }
}
