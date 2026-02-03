package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
public class EmployeeMapper {
    public Employee createEmployeeFromRequest(CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = new Employee();
        employee.setCreatedAt(LocalDateTime.now());
        employee.setDateOfBirth(LocalDate.now());
        employee.setStartDate(createEmployeeRequest.startDate());
        employee.setName(createEmployeeRequest.name());
        employee.setSurname(createEmployeeRequest.surname());
        employee.setGender(createEmployeeRequest.gender());
        employee.setDateOfBirth(createEmployeeRequest.dateOfBirth());
        employee.setNationality(createEmployeeRequest.nationality());
        employee.setDocumentID(createEmployeeRequest.documentID());
        employee.setAddress(createEmployeeRequest.address());
        employee.setPhoneNumber(createEmployeeRequest.phoneNumber());
        employee.setEmail(createEmployeeRequest.email());
        employee.setRole(createEmployeeRequest.role());

        return employee;
    }

    public static Employee updateEmployeeFromRequest(UpdateEmployeeRequest updateEmployeeRequest, Employee employeeToBeUpdated) {
        LocalDate startDate = updateEmployeeRequest.startDate();
        String name = updateEmployeeRequest.name();
        String surname = updateEmployeeRequest.surname();
        Gender gender = updateEmployeeRequest.gender();
        LocalDate dateOfBirth = updateEmployeeRequest.dateOfBirth();
        String nationality = updateEmployeeRequest.nationality();
        String address = updateEmployeeRequest.address();
        String phoneNumber = updateEmployeeRequest.phoneNumber();
        String email = updateEmployeeRequest.email();
        EmployeeRole role = updateEmployeeRequest.role();
        String documentID = updateEmployeeRequest.documentID();

        if (startDate != null) employeeToBeUpdated.setStartDate(startDate);
        if (name != null && !name.isEmpty()) employeeToBeUpdated.setName(name);
        if (surname != null && !surname.isEmpty()) employeeToBeUpdated.setSurname(surname);
        if (gender != null) employeeToBeUpdated.setGender(gender);
        if (dateOfBirth != null) employeeToBeUpdated.setDateOfBirth(dateOfBirth);
        if (nationality != null && !nationality.isEmpty()) employeeToBeUpdated.setNationality(nationality);
        if (address != null && !address.isEmpty()) employeeToBeUpdated.setAddress(address);
        if (phoneNumber != null && !phoneNumber.isEmpty()) employeeToBeUpdated.setPhoneNumber(phoneNumber);
        if (email != null && !email.isEmpty()) employeeToBeUpdated.setEmail(email);
        if (role != null) employeeToBeUpdated.setRole(role);
        if (documentID != null && !documentID.isEmpty()) employeeToBeUpdated.setDocumentID(documentID);

        return employeeToBeUpdated;
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getStartDate(),
                employee.getName(),
                employee.getSurname(),
                employee.getGender(),
                employee.getDateOfBirth(),
                employee.getNationality(),
                employee.getAddress(),
                employee.getPhoneNumber(),
                employee.getEmail(),
                employee.getRole()
        );
    }
}
