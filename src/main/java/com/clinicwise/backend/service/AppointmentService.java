package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreateAppointmentRequest;
import com.clinicwise.backend.dto.request.UpdateAppointmentRequest;
import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.mapper.AppointmentMapper;
import com.clinicwise.backend.repository.AppointmentRepository;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.service.dto.PatientEmployee;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;
    private AppointmentMapper appointmentMapper;
    private EmployeeRepository employeeRepository;
    private PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, EmployeeRepository employeeRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.employeeRepository = employeeRepository;
        this.patientRepository = patientRepository;
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    public AppointmentResponse getAppointment(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an appointment with ID: " + appointmentId));

        return AppointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest createAppointmentRequest) {
        Integer patientId = createAppointmentRequest.patientId();
        Integer employeeId = createAppointmentRequest.employeeId();
        PatientEmployee patientEmployee = checkIfPatientAndEmployeeExists(patientId, employeeId);

        Appointment appointment = appointmentMapper.createAppointmentFromRequest(createAppointmentRequest, patientEmployee.patient(), patientEmployee.employee());

        appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateAppointment(int appointmentId, UpdateAppointmentRequest updateAppointmentRequest) {
        Appointment appointmentToBeUpdated = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an appointment with ID: " + appointmentId));
        Integer patientId = updateAppointmentRequest.patientId();
        Integer employeeId = updateAppointmentRequest.employeeId();
        PatientEmployee patientEmployee = checkIfPatientAndEmployeeExists(patientId, employeeId);

        appointmentMapper.updateAppointmentFromRequest(updateAppointmentRequest, appointmentToBeUpdated, patientEmployee.employee(), patientEmployee.patient());

        return AppointmentMapper.toResponse(appointmentToBeUpdated);
    }

    @Transactional
    public void deleteAppointment(int appointmentId) {
        Appointment appointmentToBeDeleted = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an appointment with ID: " + appointmentId));

        appointmentRepository.delete(appointmentToBeDeleted);
    }

    private PatientEmployee checkIfPatientAndEmployeeExists(Integer patientId, Integer employeeId) {
        Patient patient = Optional.ofNullable(patientId)
                .map(id -> patientRepository.findById(patientId)
                        .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId)))
                .orElse(null);
        Employee employee = Optional.ofNullable(employeeId)
                .map(id -> employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with ID: " + employeeId)))
                .orElse(null);

        return new PatientEmployee(patient, employee);
    }
}
