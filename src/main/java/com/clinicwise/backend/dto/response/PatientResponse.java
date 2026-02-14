package com.clinicwise.backend.dto.response;

import com.clinicwise.backend.enums.PatientSubscriptionPlan;

import java.time.LocalDate;

public record PatientResponse(
        Integer id,
        LocalDate startDate,
        PatientSubscriptionPlan patientSubscriptionPlan,
        UserResponse user
) {
}
