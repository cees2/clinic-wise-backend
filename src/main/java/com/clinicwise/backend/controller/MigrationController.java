package com.clinicwise.backend.controller;

import com.clinicwise.backend.service.MigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migrations")
public class MigrationController {
    private MigrationService migrationService;

    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @PostMapping("/appointment_end_date")
    public ResponseEntity<Void> migrateAppointmentEndDate() {
        migrationService.migrateAppointmentEndDate();

        return ResponseEntity.ok().build();
    }
}
