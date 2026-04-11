package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.Authority;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.AuthorityType;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.faker.CustomFaker;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class EmployeeMapper {
    public Employee createEmployeeFromRequest(CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = new Employee();
        employee.setCreatedAt(LocalDateTime.now());
        employee.setStartDate(createEmployeeRequest.startDate());
        employee.setRole(createEmployeeRequest.role());

        Authority authority = new Authority();
        authority.setAuthority(AuthorityType.ROLE_EMPLOYEE);
        authority.setUsername(createEmployeeRequest.username());
        Set<Authority> authoritySet = Set.of(authority);

        User user = new User();
        user.setFirstname(createEmployeeRequest.firstname());
        user.setLastname(createEmployeeRequest.lastname());
        user.setGender(createEmployeeRequest.gender());
        user.setDateOfBirth(createEmployeeRequest.dateOfBirth());
        user.setNationality(createEmployeeRequest.nationality());
        user.setDocumentId(createEmployeeRequest.documentId());
        user.setAddress(createEmployeeRequest.address());
        user.setPhoneNumber(createEmployeeRequest.phoneNumber());
        user.setEnabled(true);
        user.setUsername(createEmployeeRequest.username());
        user.setPassword(createEmployeeRequest.password());

        authority.setUser(user);

        user.setAuthorities(authoritySet);

        employee.setUser(user);

        return employee;
    }

    public static Employee updateEmployeeFromRequest(UpdateEmployeeRequest updateEmployeeRequest, Employee employeeToBeUpdated) {
        LocalDate startDate = updateEmployeeRequest.startDate();
        String name = updateEmployeeRequest.firstname();
        String lastName = updateEmployeeRequest.lastname();
        Gender gender = updateEmployeeRequest.gender();
        LocalDate dateOfBirth = updateEmployeeRequest.dateOfBirth();
        String nationality = updateEmployeeRequest.nationality();
        String address = updateEmployeeRequest.address();
        String phoneNumber = updateEmployeeRequest.phoneNumber();
        EmployeeRole role = updateEmployeeRequest.role();
        String documentId = updateEmployeeRequest.documentId();
        String username = updateEmployeeRequest.username();
        String password = updateEmployeeRequest.password();
        Boolean enabled = updateEmployeeRequest.enabled();

        if (startDate != null) employeeToBeUpdated.setStartDate(startDate);
        if (name != null && !name.isEmpty()) employeeToBeUpdated.getUser().setFirstname(name);
        if (lastName != null && !lastName.isEmpty()) employeeToBeUpdated.getUser().setLastname(lastName);
        if (gender != null) employeeToBeUpdated.getUser().setGender(gender);
        if (dateOfBirth != null) employeeToBeUpdated.getUser().setDateOfBirth(dateOfBirth);
        if (nationality != null && !nationality.isEmpty()) employeeToBeUpdated.getUser().setNationality(nationality);
        if (address != null && !address.isEmpty()) employeeToBeUpdated.getUser().setAddress(address);
        if (phoneNumber != null && !phoneNumber.isEmpty()) employeeToBeUpdated.getUser().setPhoneNumber(phoneNumber);
        if (role != null) employeeToBeUpdated.setRole(role);
        if (documentId != null && !documentId.isEmpty()) employeeToBeUpdated.getUser().setDocumentId(documentId);
        if (username != null) employeeToBeUpdated.getUser().setUsername(username);
        if (password != null) employeeToBeUpdated.getUser().setPassword(password);
        if (enabled != null) employeeToBeUpdated.getUser().setEnabled(enabled);

        return employeeToBeUpdated;
    }

    public static EmployeeResponse toResponse(Employee employee) {
        UserResponse userResponse = UserMapper.toResponse(employee.getUser());

        return new EmployeeResponse(
                employee.getId(),
                employee.getStartDate(),
                employee.getRole(),
                userResponse
        );
    }

    public static SearchSelect toSearchSelect(Employee employee){
        String name = employee.getUser().getFirstname() + " " + employee.getUser().getLastname();

        return new SearchSelect(employee.getId(), name);
    }

    public static List<Employee> generateFakeEmployees() {
        CustomFaker faker = new CustomFaker();
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Employee employee = new Employee();
            User user = UserMapper.generateFakeUser();

            employee.setCreatedAt(LocalDateTime.now());
            employee.setStartDate(MapperUtils.randomDate7YearsTo1WeekAgo(faker));
            employee.setUser(user);
            employee.setRole(faker.employeeRole().nextEmployeeRole());
            employees.add(employee);
        }

        return employees;
    }
}
