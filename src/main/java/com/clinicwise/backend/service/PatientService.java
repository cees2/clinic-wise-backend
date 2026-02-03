package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponse> getAllPatients(){
        List<Patient> patients =  patientRepository.findAll();

        return patients.stream().map(PatientMapper::toResponse).toList();
    }
}
