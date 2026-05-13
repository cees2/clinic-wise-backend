package com.clinicwise.backend.dto.response;

import java.util.List;

public record DashboardResponse(
        List<DashboardChartData> chartData,
        List<AppointmentResponse> nextFiveAppointments,
        long numberOfAppointments,
        long workTime,
        long completedAppointments,
        long cancelledAppointments
) {
}
