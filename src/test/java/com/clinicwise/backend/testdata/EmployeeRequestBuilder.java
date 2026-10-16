package com.clinicwise.backend.testdata;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;

public class EmployeeRequestBuilder {
    private LocalDate startDate = LocalDate.now();
    private String firstname = "Damian";
    private String lastname = "Mirek";
    private Gender gender = Gender.MALE;
    private LocalDate dateOfBirth = LocalDate.now();
    private Country nationality = Country.POLAND;
    private String address = "Warsaw";
    private String phoneNumber = "123123123";
    private EmployeeRole role = EmployeeRole.DOCTOR;
    private String documentId = "AA 001";
    private String username = "damian.mirek";
    private String password = "password";

    public static EmployeeRequestBuilder anEmployeeRequest(){
        return new EmployeeRequestBuilder();
    }

    public EmployeeRequestBuilder withStartDate(LocalDate startDate){this.startDate = startDate; return this;}
    public EmployeeRequestBuilder withFirstname(String firstname){this.firstname = firstname; return this;}
    public EmployeeRequestBuilder withLastname(String lastname){this.lastname = lastname; return this;}
    public EmployeeRequestBuilder withGender(Gender gender){this.gender = gender; return this;}
    public EmployeeRequestBuilder withDateOfBirth(LocalDate dateOfBirth){this.dateOfBirth = dateOfBirth; return this;}
    public EmployeeRequestBuilder withNationality(Country nationality){this.nationality = nationality; return this;}
    public EmployeeRequestBuilder withAddress(String address){this.address = address; return this;}
    public EmployeeRequestBuilder withPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber; return this;}
    public EmployeeRequestBuilder withRole(EmployeeRole role){this.role = role; return this;}
    public EmployeeRequestBuilder withDocumentId(String documentId){this.documentId = documentId; return this;}
    public EmployeeRequestBuilder withUsername(String username){this.username = username; return this;}
    public EmployeeRequestBuilder withPassword(String password){this.password = password; return this;}

    public CreateEmployeeRequest build(){
        return new CreateEmployeeRequest(startDate, firstname, lastname, gender, dateOfBirth, nationality, address, phoneNumber, role, documentId, username, password);
    }
}
