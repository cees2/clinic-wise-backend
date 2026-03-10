package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;
    private UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public ListResponse<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponse> patientsList = patients.stream().map(PatientMapper::toResponse).toList();
        long patientsCount = patientRepository.count();

        return ListResponse.toResponse(patientsList, patientsCount);
    }

    public ApiResponse<PatientResponse> getPatient(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        return ApiResponse.toResponse(PatientMapper.toResponse(patient));
    }

    @Transactional
    public ApiResponse<PatientResponse> updatePatient(int patientId, UpdatePatientRequest updatePatientRequest) {
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        assertNoDuplicatePatient(
                patientToBeUpdated.getUser().getId(),
                updatePatientRequest.documentId(),
                updatePatientRequest.phoneNumber(),
                updatePatientRequest.username()
        );

        PatientMapper.updatePatientFromRequest(updatePatientRequest, patientToBeUpdated);

        return ApiResponse.toResponse(PatientMapper.toResponse(patientToBeUpdated));
    }

    @Transactional
    public ApiResponse<PatientResponse> createPatient(CreatePatientRequest createPatientRequest) {
        assertNoDuplicatePatient(
                null,
                createPatientRequest.documentId(),
                createPatientRequest.phoneNumber(),
                createPatientRequest.username()
        );

        Patient patient = PatientMapper.createPatientFromRequest(createPatientRequest);

        userRepository.save(patient.getUser());
        patientRepository.save(patient);

        return ApiResponse.toResponse(PatientMapper.toResponse(patient));
    }

    @Transactional
    public void deletePatient(int patientId) {
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        patientRepository.delete(patientToBeUpdated);
    }

    public ApiResponse<List<SearchSelect>> getSearchSelect(){
        List<Patient> employees = patientRepository.findAll();
        List<SearchSelect> employeesSearchSelect = employees.stream().map(PatientMapper::toSearchSelect).toList();

        return ApiResponse.toResponse(employeesSearchSelect);
    }

    private void assertNoDuplicatePatient(Integer patientId, String documentId, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrPhoneNumberOrUserName(documentId, phoneNumber, username)
                );

        if (!usersWithSimilarData.isEmpty() && !usersWithSimilarData.getFirst().getId().equals(patientId)) {
            throw new UserWithProvidedDataExists();
        }
    }
}
