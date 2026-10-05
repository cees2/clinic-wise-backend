package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateAppointmentRequest;
import com.clinicwise.backend.dto.request.UpdateAppointmentRequest;
import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.PersonalDetailsResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.AppointmentStatus;
import com.clinicwise.backend.faker.CustomFaker;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentMapper {
    private PatientMapper patientMapper;
    private EmployeeMapper employeeMapper;
    private MapperUtils mapperUtils;

    public AppointmentMapper(PatientMapper patientMapper, EmployeeMapper employeeMapper, MapperUtils mapperUtils) {
        this.patientMapper = patientMapper;
        this.employeeMapper = employeeMapper;
        this.mapperUtils = mapperUtils;
    }

    public Appointment createAppointmentFromRequest(CreateAppointmentRequest createAppointmentRequest, Patient patient, Employee employee) {
        Appointment appointment = new Appointment();

        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setDuration(createAppointmentRequest.duration());
        appointment.setStartDate(createAppointmentRequest.startDate());
        appointment.setAdditionalNote(createAppointmentRequest.additionalNote());
        appointment.setEmployee(employee);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }

    public Appointment updateAppointmentFromRequest(UpdateAppointmentRequest updateAppointmentRequest, Appointment appointment, Employee employee, Patient patient) {
        Integer duration = updateAppointmentRequest.duration();
        LocalDateTime startDate = updateAppointmentRequest.startDate();
        AppointmentStatus appointmentStatus = updateAppointmentRequest.status();
        String additionalNote = updateAppointmentRequest.additionalNote();

        if (duration != null) appointment.setDuration(duration);
        if (startDate != null) appointment.setStartDate(startDate);
        if (appointmentStatus != null) appointment.setStatus(appointmentStatus);
        if (additionalNote != null) appointment.setAdditionalNote(additionalNote);
        if (employee != null) appointment.setEmployee(employee);
        if (patient != null) appointment.setPatient(patient);

        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        PatientResponse patientResponse = patientMapper.toResponse(appointment.getPatient());
        EmployeeResponse employeeResponse = employeeMapper.toResponse(appointment.getEmployee());

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getCreatedAt(),
                appointment.getDuration(),
                appointment.getStartDate(),
                appointment.getStatus(),
                appointment.getAdditionalNote(),
                patientResponse,
                employeeResponse
        );
    }

    public List<Appointment> generateFakeAppointments(List<Patient> patients, List<Employee> employees) {
        CustomFaker faker = new CustomFaker();
        List<Appointment> appointments = new ArrayList<>();

        for(int i = 0; i < 30; i++) {
            Appointment appointment = new Appointment();
            int randomPatientIndex = faker.random().nextInt(patients.size());
            int randomEmployeeIndex = faker.random().nextInt(employees.size());
            Patient randomPatient = patients.get(randomPatientIndex);
            Employee randomEmployee = employees.get(randomEmployeeIndex);

            appointment.setCreatedAt(LocalDateTime.now());
            appointment.setDuration(faker.number().numberBetween(2, 12) * 5);
            appointment.setStartDate(mapperUtils.randomFutureDate5Days());
            appointment.setStatus(faker.appointmentStatus().nextAppointmentStatus());
            appointment.setAdditionalNote(faker.lorem().sentence());
            appointment.setEmployee(randomEmployee);
            appointment.setPatient(randomPatient);

            appointments.add(appointment);
        }

        return appointments;
    }
}
