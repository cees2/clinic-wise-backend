package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.dto.response.DashboardResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.enums.AppointmentStatus;
import net.datafaker.providers.base.App;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardMapper {
    public static DashboardResponse toResponse(List<Appointment> appointments, List<Appointment> nextFiveAppointments) {
        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(AppointmentMapper::toResponse)
                .toList();
        List<AppointmentResponse> nextFiveAppointmentsResponse = nextFiveAppointments.stream()
                .map(AppointmentMapper::toResponse)
                .toList();
        long numberOfAppointments = appointmentResponses.size();
        long numberOfCompletedAppointments = appointmentResponses.stream()
                .filter(appointment -> appointment.status().equals(AppointmentStatus.CANCELLED))
                .count();
        long numberOfCancelledAppointments = appointmentResponses.stream()
                .filter(appointment -> appointment.status().equals(AppointmentStatus.CANCELLED))
                .count();
        long workTime = appointmentResponses.stream()
                .reduce(0L, (total, appointment) -> total + appointment.duration(), Long::sum);

        return new DashboardResponse(appointmentResponses, nextFiveAppointmentsResponse, numberOfAppointments, workTime, numberOfCompletedAppointments, numberOfCancelledAppointments);
    }
}
