package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    List<PatientResponse> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{patientId}")
    PatientResponse getPatient(@PathVariable int patientId) {
        return patientService.getPatient(patientId);
    }

    @PatchMapping("/{patientId}")
    PatientResponse updatePatient(@PathVariable int patientId, @RequestBody UpdatePatientRequest updatePatientRequest) {
        return patientService.updatePatient(patientId, updatePatientRequest);
    }

    @PostMapping
    PatientResponse updatePatient(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
        return patientService.createPatient(createPatientRequest);
    }

    @DeleteMapping("/{patientId}")
    ResponseEntity<Void> deletePatient(@PathVariable int patientId) {
        patientService.deletePatient(patientId);

        return ResponseEntity.noContent().build();
    }
}
