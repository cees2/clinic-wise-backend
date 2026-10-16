package com.clinicwise.backend.testdata;

import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientBuilder {
    private Integer id = 0;
    private User user = UserBuilder.aUser().build();
    private PatientSubscriptionPlan patientSubscriptionPlan = PatientSubscriptionPlan.BASIC;
    private LocalDate startDate = LocalDate.of(2020, 1, 1);
    private LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

    public PatientBuilder withId(Integer id){this.id = id; return this;}
    public PatientBuilder withPatientSubscriptionPlan(PatientSubscriptionPlan patientSubscriptionPlan){this.patientSubscriptionPlan = patientSubscriptionPlan; return this;}
    public PatientBuilder withUser(User user){this.user = user; return this;}
    public PatientBuilder withStartDate(LocalDate startDate){this.startDate = startDate; return this;}
    public PatientBuilder withCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt; return this;}

    public static PatientBuilder aPatient(){
        PatientBuilder patientBuilder = new PatientBuilder();
        return patientBuilder;
    }

    public Patient build(){
        Patient patient = new Patient();

        patient.setId(this.id);
        patient.setUser(this.user);
        patient.setStartDate(this.startDate);
        patient.setSubscriptionPlan(this.patientSubscriptionPlan);
        patient.setCreatedAt(this.createdAt);

        return patient;
    }
}
