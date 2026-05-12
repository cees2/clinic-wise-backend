package com.clinicwise.backend.specification;

import com.clinicwise.backend.api.enums.DashboardDateRangePreset;
import com.clinicwise.backend.common.DateUtils;
import com.clinicwise.backend.common.dto.DateRange;
import com.clinicwise.backend.entity.Appointment;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardSpecification {
    public static PredicateSpecification<Appointment> appointmentsFromDateRange(DashboardDateRangePreset dateRangePreset) {
        DateRange dateRange = DateUtils.parseDateRangePresetToDateRange(dateRangePreset);

        return (root, builder) -> builder.between(root.get("startDate"), dateRange.startDate(), dateRange.endDate());
    }
}
