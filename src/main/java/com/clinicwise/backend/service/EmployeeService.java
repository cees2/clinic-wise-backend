package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.exceptions.EmployeeWithProvidedDataExists;
import com.clinicwise.backend.mapper.EmployeeMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.specification.EmployeeSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
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
        List<Employee> employeesWithMatchingDocumentIDOrEmail =
                employeeRepository.findAll(EmployeeSpecifications
                        .hasDocumentIDOrEmailOrPhoneNumber(createEmployeeRequest.documentID(), createEmployeeRequest.email(), createEmployeeRequest.phoneNumber()));

        if (!employeesWithMatchingDocumentIDOrEmail.isEmpty()) {
            throw new EmployeeWithProvidedDataExists(createEmployeeRequest.documentID());
        }

        Employee employee = employeeMapper.createEmployeeFromRequest(createEmployeeRequest);

        employeeRepository.save(employee);

        return EmployeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(int employeeId, UpdateEmployeeRequest updateEmployeeRequest) {
        Employee employeeToBeUpdated = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        EmployeeMapper.updateEmployeeFromRequest(updateEmployeeRequest, employeeToBeUpdated);

        return EmployeeMapper.toResponse(employeeToBeUpdated);
    }

    @Transactional
    public void deleteEmployee(@PathVariable int employeeId) {
        Employee employeeToBeDeleted = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        employeeRepository.delete(employeeToBeDeleted);
    }
}
