package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(PatientMapper::toResponse).toList();
    }

    public PatientResponse getPatient(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        return PatientMapper.toResponse(patient);
    }

    @Transactional
    public PatientResponse updatePatient(int patientId, UpdatePatientRequest updatePatientRequest) {
        assertNoDuplicatePatient(
                patientId,
                updatePatientRequest.documentId(),
                updatePatientRequest.email(),
                updatePatientRequest.phoneNumber(),
                updatePatientRequest.username()
        );
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        PatientMapper.updatePatientFromRequest(updatePatientRequest, patientToBeUpdated);

        return PatientMapper.toResponse(patientToBeUpdated);
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        assertNoDuplicatePatient(
                null,
                createPatientRequest.documentId(),
                createPatientRequest.email(),
                createPatientRequest.phoneNumber(),
                createPatientRequest.username()
        );

        Patient patient = PatientMapper.createPatientFromRequest(createPatientRequest);

        userRepository.save(patient.getUser());
        patientRepository.save(patient);

        return PatientMapper.toResponse(patient);
    }

    @Transactional
    public void deletePatient(int patientId) {
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        patientRepository.delete(patientToBeUpdated);
    }

    private void assertNoDuplicatePatient(Integer patientId, String documentId, String email, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrEmailOrPhoneNumberOrUserName(documentId, email, phoneNumber,username)
                );

        if (!usersWithSimilarData.isEmpty()) {
            throw new UserWithProvidedDataExists();
        }
    }
}
