package com.clinicwise.backend.dto.request;

import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;

import java.time.LocalDate;

public record UpdatePatientRequest(
        String firstname,
        String lastname,
        Gender gender,
        LocalDate dateOfBirth,
        String documentId,
        String address,
        String nationality,
        String phoneNumber,
        LocalDate startDate,
        String email,
        PatientSubscriptionPlan patientSubscriptionPlan,
        String username,
        String password
) {
}
