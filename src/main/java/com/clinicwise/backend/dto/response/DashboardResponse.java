package com.clinicwise.backend.dto.response;

import java.util.List;

public record DashboardResponse(
        List<AppointmentResponse> appointments,
        List<AppointmentResponse> nextFiveAppointments,
        long numberOfAppointments,
        long workTime,
        long completedAppointments,
        long cancelledAppointments
) {
}
