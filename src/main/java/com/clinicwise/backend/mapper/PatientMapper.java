package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;

public class PatientMapper {
    public static PatientResponse toResponse(Patient patient){
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getSurname(),
                patient.getGender(),
                patient.getDateOfBirth(),
                patient.getDocumentId(),
                patient.getAddress(),
                patient.getNationality(),
                patient.getPhoneNumber(),
                patient.getStartDate()
        );
    }
}
