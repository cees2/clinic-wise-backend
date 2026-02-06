package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.exceptions.PatientWithProvidedDataExists;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.specification.PatientSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        assertNoDuplicatePatient(patientId, updatePatientRequest.documentId(), updatePatientRequest.phoneNumber());
        PatientMapper.updatePatientFromRequest(updatePatientRequest, patientToBeUpdated);

        return PatientMapper.toResponse(patientToBeUpdated);
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        Patient patient = PatientMapper.createPatientFromRequest(createPatientRequest);

        assertNoDuplicatePatient(null, createPatientRequest.documentId(), createPatientRequest.phoneNumber());
        patientRepository.save(patient);

        return PatientMapper.toResponse(patient);
    }

    @Transactional
    public void deletePatient(int patientId) {
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        patientRepository.delete(patientToBeUpdated);
    }

    private void assertNoDuplicatePatient(Integer patientId, String documentId, String phoneNumber) {
        List<Patient> patientsWithSimilarData = patientRepository
                .findAll(
                        PatientSpecifications
                                .hasDocumentIdOrPhoneNumber(documentId, phoneNumber)
                )
                .stream()
                .filter(patient -> patientId == null || !patient.getId().equals(patientId) )
                .toList();

        if (!patientsWithSimilarData.isEmpty()) {
            throw new PatientWithProvidedDataExists();
        }
    }
}
