package com.clinicwise.backend.service;

import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MigrationService {
    private AppointmentRepository appointmentRepository;

    public MigrationService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void migrateAppointmentEndDate() {
        List<Appointment> appointmentList = appointmentRepository.findAll();

        for(Appointment appointment : appointmentList){
            LocalDateTime appointmentStartDate = appointment.getStartDate();
            int duration = appointment.getDuration();
            LocalDateTime appointmentEndDate = appointmentStartDate.plusMinutes(duration);
            appointment.setEndDate(appointmentEndDate);
            appointmentRepository.save(appointment);
        }
    }
}
