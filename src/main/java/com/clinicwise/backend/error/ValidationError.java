package com.clinicwise.backend.error;

public record ValidationError(String field, String value) {
}
