package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.AppointmentStatus;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.repository.AppointmentRepository;
import com.clinicwise.backend.testdata.AppointmentBuilder;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.PatientBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentDomainValidatorTest {
    @Mock
    AppointmentRepository appointmentRepository;
    @InjectMocks
    AppointmentDomainValidator appointmentDomainValidator;

    @Test
    @DisplayName("Should return an employee overlapping appointment")
    void shouldReturnEmployeeOverlappingAppointment(){
        LocalDateTime conflictingAppointmentStart = LocalDateTime.now();
        int overlappingAppointmentDuration = 30;
        Employee overlappingEmployee = EmployeeBuilder.anEmployee().withId(1).build();
        Patient patient1 = PatientBuilder.aPatient().withId(1).build();
        Patient patient2 = PatientBuilder.aPatient().withId(2).build();
        Appointment conflictingAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(overlappingEmployee)
                .withPatient(patient1)
                .withStartDate(conflictingAppointmentStart)
                .withDuration(overlappingAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        LocalDateTime actualAppointmentStart = conflictingAppointmentStart.minusMinutes(10);
        int actualAppointmentDuration = 30;
        Appointment actualAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(overlappingEmployee)
                .withPatient(patient2)
                .withStartDate(actualAppointmentStart)
                .withDuration(actualAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        String EXPECTED_CODE = "EMPLOYEE_BUSY";
        String EXPECTED_MESSAGE = "Employee cannot have another appointment at the same time";
        List<String> EXPECTED_INVALID_FIELDS = List.of("employee_id", "start_date", "duration");

        when(appointmentRepository.findAll(any(Specification.class))).thenReturn(List.of(conflictingAppointment));
        List<ValidationError> validationErrors = appointmentDomainValidator.findConflictingEntities(actualAppointment);

        assertThat(validationErrors).isNotEmpty();
        assertEquals(1, validationErrors.size());

        ValidationError validationError = validationErrors.getFirst();

        assertEquals(EXPECTED_CODE, validationError.code());
        assertEquals(EXPECTED_MESSAGE, validationError.message());
        assertEquals(3, validationError.fields().size());
        EXPECTED_INVALID_FIELDS.forEach(field -> assertTrue(validationError.fields().contains(field)));
//        assertEquals(1, validationError.details().get("conflicting_appointment_ids"));
    }

    @Test
    @DisplayName("Should return a patient overlapping appointment")
    void shouldReturnPatientOverlappingAppointment(){
        LocalDateTime conflictingAppointmentStart = LocalDateTime.now();
        int overlappingAppointmentDuration = 30;
        Employee employee1 = EmployeeBuilder.anEmployee().withId(1).build();
        Employee employee2 = EmployeeBuilder.anEmployee().withId(2).build();
        Patient patient = PatientBuilder.aPatient().withId(1).build();
        Appointment conflictingAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(employee1)
                .withPatient(patient)
                .withStartDate(conflictingAppointmentStart)
                .withDuration(overlappingAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        LocalDateTime actualAppointmentStart = conflictingAppointmentStart.minusMinutes(10);
        int actualAppointmentDuration = 30;
        Appointment actualAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(employee2)
                .withPatient(patient)
                .withStartDate(actualAppointmentStart)
                .withDuration(actualAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        String EXPECTED_CODE = "PATIENT_BUSY";
        String EXPECTED_MESSAGE = "Patient cannot have another appointment at the same time";
        List<String> EXPECTED_INVALID_FIELDS = List.of("patient_id", "start_date", "duration");

        when(appointmentRepository.findAll(any(Specification.class))).thenReturn(List.of(conflictingAppointment));
        List<ValidationError> validationErrors = appointmentDomainValidator.findConflictingEntities(actualAppointment);

        assertThat(validationErrors).isNotEmpty();
        assertEquals(1, validationErrors.size());

        ValidationError validationError = validationErrors.getFirst();

        assertEquals(EXPECTED_CODE, validationError.code());
        assertEquals(EXPECTED_MESSAGE, validationError.message());
        assertEquals(3, validationError.fields().size());
        EXPECTED_INVALID_FIELDS.forEach(field -> assertTrue(validationError.fields().contains(field)));
//        assertEquals(1, validationError.details().get("conflicting_appointment_ids"));
    }

    @Test
    @DisplayName("Should return both patient and employee overlapping appointment")
    void shouldReturnPatientAndEmployeeOverlappingAppointment(){
        LocalDateTime conflictingAppointmentStart = LocalDateTime.now();
        int overlappingAppointmentDuration = 30;
        Employee employee = EmployeeBuilder.anEmployee().withId(1).build();
        Patient patient = PatientBuilder.aPatient().withId(1).build();
        Appointment conflictingAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(employee)
                .withPatient(patient)
                .withStartDate(conflictingAppointmentStart)
                .withDuration(overlappingAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        LocalDateTime actualAppointmentStart = conflictingAppointmentStart.minusMinutes(10);
        int actualAppointmentDuration = 30;
        Appointment actualAppointment = AppointmentBuilder
                .anAppointment()
                .withEmployee(employee)
                .withPatient(patient)
                .withStartDate(actualAppointmentStart)
                .withDuration(actualAppointmentDuration)
                .withStatus(AppointmentStatus.SCHEDULED)
                .build();
        String EXPECTED_CODE = "PATIENT_BUSY";
        String EXPECTED_MESSAGE = "Patient cannot have another appointment at the same time";
        List<String> EXPECTED_INVALID_FIELDS = List.of("patient_id", "start_date", "duration");

        when(appointmentRepository.findAll(any(Specification.class))).thenReturn(List.of(conflictingAppointment));
        List<ValidationError> validationErrors = appointmentDomainValidator.findConflictingEntities(actualAppointment);

        assertThat(validationErrors).isNotEmpty();
        assertEquals(2, validationErrors.size());
//        assertEquals(1, validationError.details().get("conflicting_appointment_ids"));
    }
}