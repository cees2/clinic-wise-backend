package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.EmployeeMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;
    private UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.userRepository = userRepository;
    }

    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(EmployeeMapper::toResponse).toList();
    }

    public EmployeeResponse getEmployee(int id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Could not find an user with id: " + id));

        return EmployeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        assertNoDuplicateUser(null,
                createEmployeeRequest.documentId(),
                createEmployeeRequest.email(),
                createEmployeeRequest.phoneNumber(),
                createEmployeeRequest.username()
        );

        Employee employee = employeeMapper.createEmployeeFromRequest(createEmployeeRequest);
        employeeRepository.save(employee);

        return EmployeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(int employeeId, UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employeeToBeUpdated = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        assertNoDuplicateUser(
                employeeId,
                updateEmployeeRequest.documentId(),
                updateEmployeeRequest.email(),
                updateEmployeeRequest.phoneNumber(),
                updateEmployeeRequest.username()
        );
        EmployeeMapper.updateEmployeeFromRequest(updateEmployeeRequest, employeeToBeUpdated);

        return EmployeeMapper.toResponse(employeeToBeUpdated);
    }

    @Transactional
    public void deleteEmployee(@PathVariable int employeeId) {
        Employee employeeToBeDeleted = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        employeeRepository.delete(employeeToBeDeleted);
    }

    private void assertNoDuplicateUser(Integer employeeId, String documentId, String email, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrEmailOrPhoneNumberOrUserName(documentId, email, phoneNumber,username)
                );

        if (!usersWithSimilarData.isEmpty()) {
            throw new UserWithProvidedDataExists();
        }
    }
}
