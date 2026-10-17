package com.clinicwise.backend.testdata;

import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class UserBuilder {
    private static final AtomicInteger SEQ = new AtomicInteger();
    private final int n = SEQ.incrementAndGet();

    private Integer id = null;
    private String username = "user" + n;
    private String phoneNumber = "12312" + String.format("%04d", n);
    private String documentId = "AA 11" + n;
    private String firstname = "Matt";
    private String lastname = "Brown";
    private Gender gender = Gender.FEMALE;
    private LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
    private Country nationality = Country.FRANCE;

    public static UserBuilder aUser() {return new UserBuilder();}

    public UserBuilder withId(Integer id) {this.id = id; return this;}
    public UserBuilder withFirstname(String firstname) {this.firstname = firstname; return this;}
    public UserBuilder withLastname(String lastname) {this.lastname = lastname; return this;}
    public UserBuilder withPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber; return this;}
    public UserBuilder withDocumentID(String documentId) {this.documentId = documentId; return this;}
    public UserBuilder withUsername(String username) {this.username = username; return this;}
    public UserBuilder withGender(Gender gender) {this.gender = gender; return this;}
    public UserBuilder withDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth; return this;}
    public UserBuilder withNationality(Country nationality) {this.nationality = nationality; return this;}

    public User build(){
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setDocumentId(documentId);
        user.setPassword("x");
        user.setEnabled(true);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        user.setAddress("Adres");
        user.setNationality(nationality);

        return user;
    }
}
