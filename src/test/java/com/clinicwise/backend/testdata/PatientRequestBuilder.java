package com.clinicwise.backend.testdata;

import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;

import java.time.LocalDate;

public class PatientRequestBuilder {
    private String firstname = "firstname";
    private String lastname = "lastname";
    private Gender gender = Gender.MALE;
    private LocalDate dateOfBirth = LocalDate.now();
    private String documentId = "AA 222";
    private String address = "ul example 22";
    private Country nationality = Country.POLAND;
    private String phoneNumber = "111222333";
    private LocalDate startDate = LocalDate.now();
    private PatientSubscriptionPlan patientSubscriptionPlan = PatientSubscriptionPlan.BASIC;
    private String username = "username";
    private String password = "password";

    public static PatientRequestBuilder aPatientRequestBuilder() {return new PatientRequestBuilder();}

    public PatientRequestBuilder withFirstName(String firstname) {this.firstname = firstname; return this;}
    public PatientRequestBuilder withLastName(String lastname) {this.lastname = lastname; return this;}
    public PatientRequestBuilder withGender(Gender gender) {this.gender = gender; return this;}
    public PatientRequestBuilder withDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth; return this;}
    public PatientRequestBuilder withDocumentId(String documentId) {this.documentId = documentId; return this;}
    public PatientRequestBuilder withAddress(String address) {this.address = address; return this;}
    public PatientRequestBuilder withNationality(Country nationality) {this.nationality = nationality; return this;}
    public PatientRequestBuilder withPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber; return this;}
    public PatientRequestBuilder withStartDate(LocalDate startDate) {this.startDate = startDate; return this;}
    public PatientRequestBuilder withPatientSubscriptionPlan(PatientSubscriptionPlan patientSubscriptionPlan) {this.patientSubscriptionPlan = patientSubscriptionPlan; return this;}
    public PatientRequestBuilder withUsername(String username) {this.username = username; return this;}
    public PatientRequestBuilder withPassword(String password) {this.password = password; return this;}

    public CreatePatientRequest build(){
        return new CreatePatientRequest(firstname, lastname, gender, dateOfBirth, documentId, address, nationality, phoneNumber, startDate, patientSubscriptionPlan, username, password);
    }
}
