package com.clinicwise.backend.service;

import com.clinicwise.backend.api.enums.DashboardDateRangePreset;
import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.dto.response.DashboardResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.mapper.DashboardMapper;
import com.clinicwise.backend.repository.AppointmentRepository;
import com.clinicwise.backend.specification.DashboardSpecification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {
    private AppointmentRepository appointmentRepository;

    public DashboardService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public ApiResponse<DashboardResponse> getDashboardData(DashboardDateRangePreset dateRangePreset) {
        List<Appointment> appointments = appointmentRepository.findAll(DashboardSpecification.appointmentsFromDateRange(dateRangePreset));
        List<Appointment> nextFiveAppointments = appointmentRepository.findTop5ByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());

        return ApiResponse.toResponse(DashboardMapper.toResponse(appointments, nextFiveAppointments));
    }
}
