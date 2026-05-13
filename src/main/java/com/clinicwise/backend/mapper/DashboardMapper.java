package com.clinicwise.backend.mapper;

import com.clinicwise.backend.api.enums.DashboardDateRangePreset;
import com.clinicwise.backend.common.DateUtils;
import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.dto.response.DashboardChartData;
import com.clinicwise.backend.dto.response.DashboardResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.enums.AppointmentStatus;
import net.datafaker.providers.base.App;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardMapper {
    public static DashboardResponse toResponse(List<Appointment> appointments, List<Appointment> nextFiveAppointments, DashboardDateRangePreset dateRangePreset) {
        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(AppointmentMapper::toResponse)
                .toList();
        List<DashboardChartData> chartData = convertAppointmentsToDashboardChartData(appointments, dateRangePreset);
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

        return new DashboardResponse(chartData, nextFiveAppointmentsResponse, numberOfAppointments, workTime, numberOfCompletedAppointments, numberOfCancelledAppointments);
    }

    private static List<DashboardChartData> convertAppointmentsToDashboardChartData(List<Appointment> dashboardAppointments, DashboardDateRangePreset dateRangePreset) {
        List<DashboardChartData> dashboardChartData = new ArrayList<>();

        switch (dateRangePreset) {
            case TODAY -> {
                String date = LocalDate.now().format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT));
                int numberOfAppointments = dashboardAppointments.size();

                dashboardChartData.add(new DashboardChartData(numberOfAppointments, date));
            }
            case YESTERDAY -> {
                String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT));
                int numberOfAppointments = dashboardAppointments.size();

                dashboardChartData.add(new DashboardChartData(numberOfAppointments, date));
            }
            case THIS_WEEK -> {
                DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                int daysSinceMonday = dayOfWeek.getValue() - DayOfWeek.MONDAY.getValue();

                for (int i = daysSinceMonday; i >= 0; i--) {
                    LocalDate currentDate = LocalDate.now().minusDays(i);
                    String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT));
                    dashboardChartData.add(new DashboardChartData(getNumberOfAppointmentsFromDate(dashboardAppointments, currentDate), formattedDate));
                }
            }
            case LAST_7_DAYS -> {
                for (int i = 6; i >= 0; i--) {
                    LocalDate currentDate = LocalDate.now().minusDays(i);
                    String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT));
                    dashboardChartData.add(new DashboardChartData(getNumberOfAppointmentsFromDate(dashboardAppointments, currentDate), formattedDate));
                }
            }
            case LAST_30_DAYS -> {
                for (int i = 29; i >= 0; i--) {
                    LocalDate currentDate = LocalDate.now().minusDays(i);
                    String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT));
                    dashboardChartData.add(new DashboardChartData(getNumberOfAppointmentsFromDate(dashboardAppointments, currentDate), formattedDate));
                }
            }
        }

        return dashboardChartData;
    }

    private static int getNumberOfAppointmentsFromDate(List<Appointment> dashboardAppointments, LocalDate currentDate) {
        return dashboardAppointments.stream()
                .filter(appointment -> appointment.getStartDate().toLocalDate().equals(currentDate))
                .toList()
                .size();
    }
}
