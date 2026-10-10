package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.common.list.filter.AppointmentsFilter;
import com.clinicwise.backend.dto.request.CreateAppointmentRequest;
import com.clinicwise.backend.dto.request.UpdateAppointmentRequest;
import com.clinicwise.backend.dto.response.AppointmentResponse;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.AppointmentStatus;
import com.clinicwise.backend.mapper.AppointmentMapper;
import com.clinicwise.backend.repository.AppointmentRepository;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentService appointmentService;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PatientRepository patientRepository;

    @Test
    @DisplayName("Should cancel an appointment when it is scheduled")
    void cancelAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        AppointmentResponse appointmentResponse = mock(AppointmentResponse.class);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        ApiResponse<AppointmentResponse> response = appointmentService.cancelAppointment(1);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertSame(appointmentResponse, response.getData());
    }

    @DisplayName("Should not let cancel any appointment with status different than scheduled")
    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SCHEDULED")
    void cancelAppointmentOnlyWithGivenStatus(AppointmentStatus status){
        Appointment appointmentCancelled = new Appointment();
        appointmentCancelled.setStatus(status);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointmentCancelled));

        assertThrows(IllegalStateException.class, () -> appointmentService.cancelAppointment(1));
        assertEquals(status, appointmentCancelled.getStatus());
    }

    @Test
    @DisplayName("Should throw the Resource not found error on no appointment found")
    void cancelAppointmentThrowsResourceNotFoundException() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.cancelAppointment(1));
    }

    @Test
    @DisplayName("Should not remove an appointment if no such appointment has been found")
    void throwOnAppointmentNotFound() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.deleteAppointment(1));
    }

    @Test
    @DisplayName("Should not update an appointment if no such appointment has been found")
    void throwOnUpdateAppointmentNotFound() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment(1, new UpdateAppointmentRequest(null, null, null, null, null, null)));
    }

    @Test
    @DisplayName("Should not get an appointment if no such appointment has been found")
    void throwOnGetAppointmentNotFound() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.getAppointment(1));
    }

    @Test
    @DisplayName("Should not create an appointment if no such patient has been found")
    void throwOnCreateAppointmentPatientNotFound() {
        CreateAppointmentRequest createAppointmentRequest = new CreateAppointmentRequest(90, LocalDateTime.now(), "", 1, 1);
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.createAppointment(createAppointmentRequest));
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not create an appointment if no such employee has been found")
    void throwOnCreateAppointmentEmployeeNotFound() {
        CreateAppointmentRequest createAppointmentRequest = new CreateAppointmentRequest(90, LocalDateTime.now(), "", 1, 1);
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());
        when(patientRepository.findById(1)).thenReturn(Optional.of(new Patient()));

        assertThrows(EntityNotFoundException.class, () -> appointmentService.createAppointment(createAppointmentRequest));
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not update an appointment if no such patient has been found")
    void throwOnUpdateAppointmentPatientNotFound() {
        UpdateAppointmentRequest updateAppointmentRequest = new UpdateAppointmentRequest(10, LocalDateTime.now(), AppointmentStatus.SCHEDULED, "", 1, 1);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(new Appointment()));
        when(patientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment(1, updateAppointmentRequest));
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    @DisplayName("Should not update an appointment if no such employee has been found")
    void throwOnUpdateAppointmentEmployeeNotFound() {
        UpdateAppointmentRequest updateAppointmentRequest = new UpdateAppointmentRequest(10, LocalDateTime.now(), AppointmentStatus.SCHEDULED, "", 1, 1);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(new Appointment()));
        when(patientRepository.findById(1)).thenReturn(Optional.of(new Patient()));
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment(1, updateAppointmentRequest));
        verifyNoInteractions(appointmentMapper);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    @DisplayName("Should return given number of appointments and has next set to true when needed")
    void returnHasNextInfoAndGivenSizeWhenNeeded(int size) {
        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(size, 0, "", "", null, null, null,null);
        when(appointmentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(getMockAppointments(size + 1)));

        ListResponse<AppointmentResponse> response = appointmentService.getAllAppointments(appointmentsFilter);

        assertEquals(size, response.getData().size());
        assertTrue(response.isHasNext());
    }

    @Test
    @DisplayName("Should return 15 appointments for size 20 and has next set to false")
    void return15AppointmentsAndFalse() {
        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, "", "", null, null, null,null);
        when(appointmentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(getMockAppointments(15)));

        ListResponse<AppointmentResponse> response = appointmentService.getAllAppointments(appointmentsFilter);

        assertEquals(15, response.getData().size());
        assertFalse(response.isHasNext());
    }

    private List<Appointment> getMockAppointments(int size){
        List<Appointment> appointments = new ArrayList<>();

        for(int i = 0; i < size; i++){
            appointments.add(new Appointment());
        }

        return appointments;
    }
}