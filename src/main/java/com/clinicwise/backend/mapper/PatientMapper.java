package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.Authority;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.AuthorityType;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;
import com.clinicwise.backend.faker.CustomFaker;
import net.datafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PatientMapper {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public static Patient createPatientFromRequest(CreatePatientRequest createPatientRequest) {
        Patient patient = new Patient();
        patient.setStartDate(createPatientRequest.startDate());
        patient.setCreatedAt(LocalDateTime.now());
        patient.setSubscriptionPlan(createPatientRequest.patientSubscriptionPlan());

        Authority authority = new Authority();
        authority.setUsername(createPatientRequest.username());
        authority.setAuthority(AuthorityType.ROLE_PATIENT);
        Set<Authority> authorities = Set.of(authority);

        User user = new User();
        authority.setUser(user);
        user.setFirstname(createPatientRequest.firstname());
        user.setLastname(createPatientRequest.lastname());
        user.setGender(createPatientRequest.gender());
        user.setDateOfBirth(createPatientRequest.dateOfBirth());
        user.setDocumentId(createPatientRequest.documentId());
        user.setAddress(createPatientRequest.address());
        user.setNationality(createPatientRequest.nationality());
        user.setPhoneNumber(createPatientRequest.phoneNumber());
        user.setUsername(createPatientRequest.username());
        user.setPassword(bCryptPasswordEncoder.encode(createPatientRequest.password()));
        user.setEnabled(true);
        user.setAuthorities(authorities);

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

    public static SearchSelect toSearchSelect(Patient patient) {
        String name = patient.getUser().getFirstname() + " " + patient.getUser().getLastname();

        return new SearchSelect(patient.getId(), name);
    }

    public static List<Patient> generateFakePatients() {
        CustomFaker faker = new CustomFaker();
        List<Patient> patients = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Patient patient = new Patient();
            User user = UserMapper.generateFakeUser();

            patient.setCreatedAt(LocalDateTime.now());
            patient.setStartDate(MapperUtils.randomDate7YearsTo1WeekAgo());
            patient.setUser(user);
            patient.setSubscriptionPlan(faker.patientSubscriptionPlan().nextSubscriptionPlan());

            patients.add(patient);
        }

        return patients;
    }
}
