package com.clinicwise.backend.common.dto;

import java.time.LocalDateTime;

public record DateRange(
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
