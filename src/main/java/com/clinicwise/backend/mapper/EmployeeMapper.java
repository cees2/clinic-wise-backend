package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.entity.Employee;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
public class EmployeeMapper {
    public Employee createEmployeeFromRequest(CreateEmployeeRequest createEmployeeRequest){
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

    public static EmployeeResponse toResponse(Employee employee){
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
