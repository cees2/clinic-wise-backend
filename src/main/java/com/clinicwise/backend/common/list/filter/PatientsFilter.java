package com.clinicwise.backend.common.list.filter;

public class PatientsFilter extends BaseFilter{
    private String name;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private String nationality;

    public PatientsFilter(int size, int page, String sort, String name, String surname, String dateOfBirth, String gender, String nationality) {
        super(size, page, sort);
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDate_of_birth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
