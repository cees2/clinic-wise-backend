package com.clinicwise.backend.error;

import java.util.List;
import java.util.Map;

public record ValidationError(String code, String message, List<String> fields, Map<String, Object> details) {
}
