package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientMapper {
    public static Patient createPatientFromRequest(CreatePatientRequest createPatientRequest) {
        Patient patient = new Patient();
        patient.setStartDate(createPatientRequest.startDate());
        patient.setCreatedAt(LocalDateTime.now());
        patient.setSubscriptionPlan(createPatientRequest.patientSubscriptionPlan());

        User user = new User();
        user.setFirstname(createPatientRequest.firstname());
        user.setLastname(createPatientRequest.lastname());
        user.setGender(createPatientRequest.gender());
        user.setDateOfBirth(createPatientRequest.dateOfBirth());
        user.setDocumentId(createPatientRequest.documentId());
        user.setAddress(createPatientRequest.address());
        user.setNationality(createPatientRequest.nationality());
        user.setPhoneNumber(createPatientRequest.phoneNumber());
        user.setEmail(createPatientRequest.email());
        user.setEnabled(createPatientRequest.enabled());
        user.setUsername(createPatientRequest.username());
        user.setPassword(createPatientRequest.password());

        patient.setUser(user);

        return patient;
    }

    public static Patient updatePatientFromRequest(UpdatePatientRequest updatePatientRequest, Patient patientToBeUpdated) {
        String firstname = updatePatientRequest.firstname();
        String lastname = updatePatientRequest.lastname();
        Gender gender = updatePatientRequest.gender();
        LocalDate dateOfBirth = updatePatientRequest.dateOfBirth();
        String documentId = updatePatientRequest.documentId();
        String address = updatePatientRequest.address();
        String nationality = updatePatientRequest.nationality();
        String phoneNumber = updatePatientRequest.phoneNumber();
        LocalDate startDate = updatePatientRequest.startDate();
        PatientSubscriptionPlan patientSubscriptionPlan = updatePatientRequest.patientSubscriptionPlan();
        String email = updatePatientRequest.email();
        String username = updatePatientRequest.username();
        String password = updatePatientRequest.password();


        if (firstname != null) patientToBeUpdated.getUser().setFirstname(firstname);
        if (lastname != null) patientToBeUpdated.getUser().setLastname(lastname);
        if (gender != null) patientToBeUpdated.getUser().setGender(gender);
        if (dateOfBirth != null) patientToBeUpdated.getUser().setDateOfBirth(dateOfBirth);
        if (documentId != null) patientToBeUpdated.getUser().setDocumentId(documentId);
        if (address != null) patientToBeUpdated.getUser().setAddress(address);
        if (nationality != null) patientToBeUpdated.getUser().setNationality(nationality);
        if (phoneNumber != null) patientToBeUpdated.getUser().setPhoneNumber(phoneNumber);
        if (startDate != null) patientToBeUpdated.setStartDate(startDate);
        if (patientSubscriptionPlan != null) patientToBeUpdated.setSubscriptionPlan(patientSubscriptionPlan);
        if (email != null) patientToBeUpdated.getUser().setEmail(email);
        if (username != null) patientToBeUpdated.getUser().setUsername(username);
        if (password != null) patientToBeUpdated.getUser().setPassword(password);

        return patientToBeUpdated;
    }

    public static PatientResponse toResponse(Patient patient) {
        UserResponse userResponse = UserMapper.toResponse(patient.getUser());

        return new PatientResponse(
                patient.getId(),
                patient.getStartDate(),
                patient.getSubscriptionPlan(),
                userResponse
        );
    }
}
