package com.clinicwise.backend.common;

import com.clinicwise.backend.api.enums.DashboardDateRangePreset;
import com.clinicwise.backend.common.dto.DateRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public final static String DB_DATE_FORMAT_WITH_TIME = "yyyy-MM-dd HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DB_DATE_FORMAT_WITH_TIME);

    public static DateRange parseDateRangePresetToDateRange(DashboardDateRangePreset dateRangePreset){
        LocalDateTime startLocalDateTime = null;
        LocalDateTime endLocalDateTime = null;

        switch(dateRangePreset){
            case DashboardDateRangePreset.TODAY -> {
                startLocalDateTime = LocalDate.now().atStartOfDay();
                endLocalDateTime = LocalDate.now().atTime(LocalTime.MAX);
            }
            case DashboardDateRangePreset.YESTERDAY -> {
                startLocalDateTime = LocalDate.now().minusDays(1).atStartOfDay();
                endLocalDateTime = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
            }
            case DashboardDateRangePreset.THIS_WEEK -> {
                startLocalDateTime = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                endLocalDateTime = LocalDateTime.now();
            }
            case DashboardDateRangePreset.LAST_7_DAYS -> {
                startLocalDateTime = LocalDate.now().minusDays(7).atStartOfDay();
                endLocalDateTime = LocalDateTime.now();
            }
            case DashboardDateRangePreset.LAST_30_DAYS -> {
                startLocalDateTime = LocalDate.now().minusDays(30).atStartOfDay();
                endLocalDateTime = LocalDateTime.now();
            }
        }

        return new DateRange(startLocalDateTime, endLocalDateTime);
    };
}
