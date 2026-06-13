package com.clinicwise.backend.common.list.filter;

public class EmployeesFilter extends BaseFilter {
    private String name;
    private String lastname;
    private String gender;
    private String startDate;
    private String dateOfBirth;
    private String nationality;

    public EmployeesFilter(int size, int page, String sort, String name, String lastname, String gender, String startDate, String dateOfBirth, String nationality) {
        super(size, page, sort);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.startDate = startDate;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStart_date(String startDate) {
        this.startDate = startDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDate_of_birth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
