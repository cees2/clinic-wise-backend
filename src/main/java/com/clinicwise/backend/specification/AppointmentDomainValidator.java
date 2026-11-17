package com.clinicwise.backend.specification;

import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.error.ValidationError;
import com.clinicwise.backend.error.specificationError.DomainValidator;
import com.clinicwise.backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentDomainValidator extends DomainValidator<Appointment> {
    private final AppointmentRepository appointmentRepository;

    public AppointmentDomainValidator(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    protected List<ValidationError> findConflictingEntities(Appointment appointment) {
        List<Appointment> conflicting = appointmentRepository.findAll(AppointmentSpecification.findOverlapping(appointment));
        List<ValidationError> validationErrors = new ArrayList<>();

        if (conflicting.isEmpty()) return validationErrors;

        List<Integer> conflictingPatients = conflicting.stream().filter(c -> c.getPatient().getId().equals(appointment.getPatient().getId())).map(Appointment::getId).toList();
        List<Integer> conflictingEmployees = conflicting.stream().filter(c -> c.getEmployee().getId().equals(appointment.getEmployee().getId())).map(Appointment::getId).toList();

        if(!conflictingPatients.isEmpty()){
            validationErrors.add(new ValidationError("PATIENT_BUSY", "Patient cannot have another appointment at the same time", List.of("patient_id", "start_date", "duration"), Map.of("conflicting_appointment_ids", conflictingPatients)));
        }

        if(!conflictingEmployees.isEmpty()){
            validationErrors.add(new ValidationError("EMPLOYEE_BUSY", "Employee cannot have another appointment at the same time", List.of("employee_id", "start_date", "duration"), Map.of("conflicting_appointment_ids", conflictingEmployees)));
        }


        return validationErrors;
    }
}
