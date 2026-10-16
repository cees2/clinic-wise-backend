package com.clinicwise.backend.testdata;

import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.EmployeeRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeBuilder {
    private User user = UserBuilder.aUser().build();
    private LocalDate startDate = LocalDate.of(2020, 1, 1);
    private EmployeeRole employeeRole = EmployeeRole.DOCTOR;
    private LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 0,0,0);

    public EmployeeBuilder withUser(User user){this.user = user; return this;}
    public EmployeeBuilder withStartDate(LocalDate startDate){this.startDate = startDate; return this;}
    public EmployeeBuilder withEmployeeRole(EmployeeRole employeeRole){this.employeeRole = employeeRole; return this;}
    public EmployeeBuilder withCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt; return this;}
    public EmployeeBuilder withBirthDate(LocalDate birthDate) {this.user.setDateOfBirth(birthDate); return this;}
    public EmployeeBuilder withNationality(Country country) {this.user.setNationality(country); return this;}

    public static EmployeeBuilder anEmployee(){
        EmployeeBuilder employeeBuilder = new EmployeeBuilder();
        return employeeBuilder;
    }

    public Employee build(){
        Employee employee = new Employee();

        employee.setUser(this.user);
        employee.setRole(this.employeeRole);
        employee.setStartDate(this.startDate);
        employee.setCreatedAt(this.createdAt);

        return employee;
    }

}
