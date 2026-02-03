package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    List<PatientResponse> getAllPatients(){
        return patientService.getAllPatients();
    }
}
