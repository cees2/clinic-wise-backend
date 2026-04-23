package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.response.DashboardResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.mapper.DashboardMapper;
import com.clinicwise.backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    private AppointmentRepository appointmentRepository;

    public DashboardService(AppointmentRepository appointmentService) {
        this.appointmentRepository = appointmentRepository;
    }


    public DashboardResponse getDashboardData(String filter) {
        List<Appointment> appointments = appointmentRepository.findAll();

        return DashboardMapper.toResponse(appointments);
    }
}
