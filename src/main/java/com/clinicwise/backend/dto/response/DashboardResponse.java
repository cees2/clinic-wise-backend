package com.clinicwise.backend.dto.response;

import java.util.List;

public record DashboardResponse(
        List<AppointmentResponse> appointments,
        long numberOfAppointments,
        float workTime,
        long completedAppointments,
        long cancelledAppointments
) {
}
