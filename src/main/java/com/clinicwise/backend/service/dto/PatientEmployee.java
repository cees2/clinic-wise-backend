package com.clinicwise.backend.service.dto;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;

public record PatientEmployee(
        Patient patient,
        Employee employee
) {
}
