package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public class PatientMapper {
    public static Patient createPatientFromRequest(CreatePatientRequest createPatientRequest){
        Patient patient = new Patient();
        patient.setName(createPatientRequest.name());
        patient.setSurname(createPatientRequest.surname());
        patient.setGender(createPatientRequest.gender());
        patient.setDateOfBirth(createPatientRequest.dateOfBirth());
        patient.setDocumentId(createPatientRequest.documentId());
        patient.setAddress(createPatientRequest.address());
        patient.setNationality(createPatientRequest.nationality());
        patient.setPhoneNumber(createPatientRequest.phoneNumber());
        patient.setStartDate(createPatientRequest.startDate());

        return patient;
    }

    public static Patient updatePatientFromRequest(UpdatePatientRequest updatePatientRequest, Patient patientToBeUpdated){
        String name = updatePatientRequest.name();
        String surname = updatePatientRequest.surname();
        Gender gender = updatePatientRequest.gender();
        LocalDate dateOfBirth = updatePatientRequest.dateOfBirth();
        String documentId = updatePatientRequest.documentId();
        String address = updatePatientRequest.address();
        String nationality = updatePatientRequest.nationality();
        String phoneNumber = updatePatientRequest.phoneNumber();
        LocalDate startDate = updatePatientRequest.startDate();

        if(name != null) patientToBeUpdated.setName(name);
        if(surname != null) patientToBeUpdated.setSurname(surname);
        if(gender != null) patientToBeUpdated.setGender(gender);
        if(dateOfBirth != null) patientToBeUpdated.setDateOfBirth(dateOfBirth);
        if(documentId != null) patientToBeUpdated.setDocumentId(documentId);
        if(address != null) patientToBeUpdated.setAddress(address);
        if(nationality != null) patientToBeUpdated.setNationality(nationality);
        if(phoneNumber != null) patientToBeUpdated.setPhoneNumber(phoneNumber);
        if(startDate != null) patientToBeUpdated.setStartDate(startDate);

        return patientToBeUpdated;
    }

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
