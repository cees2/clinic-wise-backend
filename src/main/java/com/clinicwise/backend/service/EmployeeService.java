package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
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

    public ListResponse<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponse> patientsList = employees.stream().map(EmployeeMapper::toResponse).toList();
        long count = employeeRepository.count();

        return ListResponse.toResponse(patientsList, count);
    }

    public ApiResponse<EmployeeResponse> getEmployee(int id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Could not find an user with id: " + id));

        return ApiResponse.toResponse(EmployeeMapper.toResponse(employee));
    }

    @Transactional
    public ApiResponse<EmployeeResponse> createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        assertNoDuplicateUser(null,
                createEmployeeRequest.documentId(),
                createEmployeeRequest.phoneNumber(),
                createEmployeeRequest.username()
        );

        Employee employee = employeeMapper.createEmployeeFromRequest(createEmployeeRequest);

        employeeRepository.save(employee);

        return ApiResponse.toResponse(EmployeeMapper.toResponse(employee));
    }

    @Transactional
    public ApiResponse<EmployeeResponse> updateEmployee(int employeeId, UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employeeToBeUpdated = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        assertNoDuplicateUser(
                employeeToBeUpdated.getUser().getId(),
                updateEmployeeRequest.documentId(),
                updateEmployeeRequest.phoneNumber(),
                updateEmployeeRequest.username()
        );
        EmployeeMapper.updateEmployeeFromRequest(updateEmployeeRequest, employeeToBeUpdated);

        return ApiResponse.toResponse(EmployeeMapper.toResponse(employeeToBeUpdated));
    }

    @Transactional
    public void deleteEmployee(int employeeId) {
        Employee employeeToBeDeleted = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        employeeRepository.delete(employeeToBeDeleted);
    }

    public ApiResponse<List<SearchSelect>> getSearchSelect(){
        List<Employee> employees = employeeRepository.findAll();
        List<SearchSelect> employeesSearchSelect = employees.stream()
                .map(EmployeeMapper::toSearchSelect)
                .toList();

        return ApiResponse.toResponse(employeesSearchSelect);
    }

    private void assertNoDuplicateUser(Integer userId, String documentId, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrPhoneNumberOrUserName(documentId, phoneNumber, username)
                );

        if (!usersWithSimilarData.isEmpty() && !usersWithSimilarData.getFirst().getId().equals(userId)) {
            throw new UserWithProvidedDataExists();
        }
    }
}
