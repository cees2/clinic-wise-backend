package com.clinicwise.backend.testdata;

import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentBuilder {
    private LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 0,0,0,0);
    private LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0,0,0,0);
    private int duration = 10;
    private AppointmentStatus appointmentStatus = AppointmentStatus.SCHEDULED;
    private String additionalNote = "";
    private Employee employee = EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build();
    private Patient patient = PatientBuilder.aPatient().withUser(UserBuilder.aUser().build()).build();

    public static AppointmentBuilder anAppointment(){return new AppointmentBuilder();}

    public AppointmentBuilder withCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt; return this;}
    public AppointmentBuilder withStartDate(LocalDateTime startDate){this.startDate = startDate; return this;}
    public AppointmentBuilder withDuration(int duration){this.duration = duration; return this;}
    public AppointmentBuilder withAppointmentStatus(AppointmentStatus appointmentStatus){this.appointmentStatus = appointmentStatus; return this;}
    public AppointmentBuilder withAdditionalNote(String additionalNote){this.additionalNote = additionalNote; return this;}
    public AppointmentBuilder withEmployee(Employee employee){this.employee = employee; return this;}
    public AppointmentBuilder withPatient(Patient patient){this.patient = patient; return this;}
    public AppointmentBuilder withStatus(AppointmentStatus appointmentStatus) {this.appointmentStatus = appointmentStatus; return this;}

    public Appointment build(){
        Appointment appointment = new Appointment();

        appointment.setCreatedAt(createdAt);
        appointment.setStartDate(startDate);
        appointment.setDuration(duration);
        appointment.setStatus(appointmentStatus);
        appointment.setAdditionalNote(additionalNote);
        appointment.setEmployee(employee);
        appointment.setPatient(patient);

        return appointment;
    }
}
