package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.common.list.filter.PatientsFilter;
import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.testdata.PatientBuilder;
import com.clinicwise.backend.testdata.UserBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PatientService patientService;

    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    @DisplayName("Should return given number of patients and has next set to true")
    void getAllPatients(int limit) {
        when(patientRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(getPatientList(limit + 1)));

        PatientsFilter patientsFilter = new PatientsFilter(limit, 0, null, null, null, null, null, null, null);
        PageResponse<PatientResponse> response = patientService.getAllPatients(patientsFilter);

        assertEquals(limit, response.getData().size());
        assertTrue(response.isHasMore());
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    @DisplayName("Should return given number of patients and has next set to false")
    void getAllPatientsHasNextFalse(int limit) {
        when(patientRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(getPatientList(limit)));

        PatientsFilter patientsFilter = new PatientsFilter(limit, 0, null, null, null, null, null, null, null);
        PageResponse<PatientResponse> response = patientService.getAllPatients(patientsFilter);

        assertEquals(limit, response.getData().size());
        assertFalse(response.isHasMore());
    }

    private List<Patient> getPatientList(int limit) {
        List<Patient> patients = new ArrayList<>();

        for(int i = 0; i < limit; i++)
            patients.add(new Patient());

        return patients;
    }

    @Test
    @DisplayName("Should not return a patient if such patient does not exist")
    void getPatientThrows(){
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.getPatient(1));
    }

    @Test
    @DisplayName("Should not update a patient if such patient does not exist")
    void updatePatientThrows(){
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.updatePatient(1, new UpdatePatientRequest(null, null, null, null, null, null, null, null, null, null, null, null)));
        verify(patientMapper, never()).updatePatientFromRequest(any(), any());
    }

    @Test
    @DisplayName("Should not update a patient when conflicting data exists")
    void updateThrowIfPatientWithPhoneNumberExists(){
        String existingPhoneNumber = "111111111";
        Patient patientToBeUpdated = PatientBuilder.aPatient().withUser(UserBuilder.aUser().withId(1).withPhoneNumber("123123123").build()).build();
        Patient existingPatient = PatientBuilder.aPatient().withUser(UserBuilder.aUser().withId(2).withPhoneNumber(existingPhoneNumber).build()).build();
        UpdatePatientRequest updatePatientRequest = new UpdatePatientRequest("Damian", "Mirek", Gender.FEMALE, LocalDate.now(), "", "", Country.FRANCE, existingPhoneNumber, LocalDate.now(), PatientSubscriptionPlan.BASIC, "", "");

        when(patientRepository.findById(1)).thenReturn(Optional.of(patientToBeUpdated));
        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(patientToBeUpdated.getUser(), existingPatient.getUser()));

        assertThrows(UserWithProvidedDataExists.class, () -> patientService.updatePatient(1, updatePatientRequest));
        verify(patientMapper, never()).updatePatientFromRequest(any(), any());
    }

    @Test
    @DisplayName("Should not create a patient when conflicting data exists")
    void createThrowIfPatientWithPhoneNumberExists(){
        String existingPhoneNumber = "111111111";
        Patient existingPatient = PatientBuilder.aPatient().withUser(UserBuilder.aUser().withId(2).withPhoneNumber(existingPhoneNumber).build()).build();
        CreatePatientRequest createPatientRequest = new CreatePatientRequest("Damian", "Mirek", Gender.FEMALE, LocalDate.now(), "", "", Country.FRANCE, existingPhoneNumber, LocalDate.now(), PatientSubscriptionPlan.BASIC, "", "");

        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(existingPatient.getUser()));

        assertThrows(UserWithProvidedDataExists.class, () -> patientService.createPatient(createPatientRequest));

        verify(patientMapper, never()).createPatientFromRequest(any());
        verify(userRepository, never()).save(any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not delete a patient if no such patient exists")
    void throwsOnDelete() {
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.deletePatient(1));
    }

    @Test
    @DisplayName("Should successfully update a patient")
    void updatePatientSuccessfully(){
        Patient patientToBeUpdated = PatientBuilder.aPatient().withUser(UserBuilder.aUser().withId(1).build()).build();
        UpdatePatientRequest updatePatientRequest = new UpdatePatientRequest("Damian", "Mirek", Gender.FEMALE, LocalDate.now(), "", "", Country.FRANCE, "111222333", LocalDate.now(), PatientSubscriptionPlan.BASIC, "", "");

        when(patientRepository.findById(1)).thenReturn(Optional.of(patientToBeUpdated));
        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(patientToBeUpdated.getUser()));

        assertDoesNotThrow(() -> patientService.updatePatient(1, updatePatientRequest));
        verify(patientMapper).updatePatientFromRequest(updatePatientRequest, patientToBeUpdated);
    }

    @Test
    @DisplayName("Should successfully get a patient")
    void getPatientSuccessfully(){
        Patient patient = PatientBuilder.aPatient().withUser(UserBuilder.aUser().withId(1).build()).build();

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        assertDoesNotThrow(() -> patientService.getPatient(1));
        verify(patientMapper).toResponse(patient);
    }

    @Test
    @DisplayName("Should allow to create a new patient if no patient exists yet")
    void createIfNoPatientExists(){
        CreatePatientRequest createPatientRequest = new CreatePatientRequest("Damian", "Mirek", Gender.FEMALE, LocalDate.now(), "", "", Country.FRANCE, "111223332", LocalDate.now(), PatientSubscriptionPlan.BASIC, "", "");

        when(patientMapper.createPatientFromRequest((createPatientRequest))).thenReturn(PatientBuilder.aPatient().withUser(UserBuilder.aUser().build()).build());
        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> patientService.createPatient(createPatientRequest));
        verify(patientMapper).createPatientFromRequest(createPatientRequest);
        verify(userRepository).save(any());
        verify(patientRepository).save(any());
    }

    @Test
    @DisplayName("Should successfully remove a patient")
    void successfullyRemoveAPatient(){
        Patient patient = PatientBuilder.aPatient().withId(1).build();

        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));

        assertDoesNotThrow(() -> patientService.deletePatient(1));
        verify(patientRepository).delete(patient);
    }

//    TODO: search select
}