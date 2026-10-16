package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.common.list.sort.SortUtil;
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
import com.clinicwise.backend.specification.EmployeesSpecification;
import com.clinicwise.backend.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    private static final Map<String, String> sortMap = Map.of(
            "name", "user.firstname",
            "lastname", "user.lastname",
            "gender", "user.gender",
            "start_date", "startDate",
            "date_of_birth", "user.dateOfBirth",
            "nationality", "user.nationality",
            "role", "role"
    );
    private EmployeeRepository employeeRepository;
    private EmployeeMapper employeeMapper;
    private UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.userRepository = userRepository;
    }

    public ListResponse<EmployeeResponse> getAllEmployees(EmployeesFilter employeeFilter) {
        Sort sort = SortUtil.parseFromString(employeeFilter.getSort(), sortMap);
        Pageable pageable = PageRequest.of(employeeFilter.getPage(), employeeFilter.getSize() + 1, sort);
        Page<Employee> employees = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeeFilter), pageable);
        List<EmployeeResponse> employeesList = employees.stream()
                .limit(employeeFilter.getSize())
                .map(employeeMapper::toResponse)
                .toList();
        boolean hasNext = employees.getNumberOfElements() == employeeFilter.getSize() + 1;

        return ListResponse.toResponse(employeesList, hasNext);
    }

    public ApiResponse<EmployeeResponse> getEmployee(int id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Could not find an user with id: " + id));

        return ApiResponse.toResponse(employeeMapper.toResponse(employee));
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

        return ApiResponse.toResponse(employeeMapper.toResponse(employee));
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
        employeeMapper.updateEmployeeFromRequest(updateEmployeeRequest, employeeToBeUpdated);

        return ApiResponse.toResponse(employeeMapper.toResponse(employeeToBeUpdated));
    }

    @Transactional
    public void deleteEmployee(int employeeId) {
        Employee employeeToBeDeleted = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find an employee with given ID"));

        employeeRepository.delete(employeeToBeDeleted);
    }

    public ApiResponse<List<SearchSelect>> getSearchSelect() {
        List<Employee> employees = employeeRepository.findAll();
        List<SearchSelect> employeesSearchSelect = employees.stream()
                .map(employeeMapper::toSearchSelect)
                .toList();

        return ApiResponse.toResponse(employeesSearchSelect);
    }

    private void assertNoDuplicateUser(Integer userId, String documentId, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrPhoneNumberOrUserName(documentId, phoneNumber, username)
                );

        if (usersWithSimilarData.stream().anyMatch(user -> !user.getId().equals(userId))) {
            throw new UserWithProvidedDataExists();
        }
    }

    @Transactional
    public ApiResponse<List<EmployeeResponse>> generateEmployees() {
        List<Employee> employees = employeeMapper.generateFakeEmployees();

        employeeRepository.saveAll(employees);

        return ApiResponse.toResponse(employees
                .stream()
                .map(employeeMapper::toResponse)
                .toList()
        );
    }
}
