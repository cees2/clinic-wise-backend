package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.EmployeeMapper;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    EmployeeService employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    @DisplayName("Should return given number of employees and the has next flag set to true")
    void getAllEmployees(int size) {
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(getEmployeeList(size + 1)));

        ListResponse<EmployeeResponse> response = employeeService.getAllEmployees(new EmployeesFilter(size, 0, "", "", "", "", "", "", "", ""));

        assertTrue(response.isHasNext());
        assertEquals(size, response.getData().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {15, 20})
    @DisplayName("Should return the exact number of employees and the has next flag set to false if there is no more employees")
    void getAll15EmployeesFalse(int size){
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(getEmployeeList(size)));

        ListResponse<EmployeeResponse> response = employeeService.getAllEmployees(new EmployeesFilter(20, 0, "", "", "", "", "", "", "", ""));

        assertFalse(response.isHasNext());
        assertEquals(size, response.getData().size());
    }

    private List<Employee> getEmployeeList(int size){
        List<Employee> employees = new ArrayList<>();

        for(int i = 0; i < size; i++) employees.add(new Employee());

        return employees;
    }

    @Test
    @DisplayName("Should not create employee when user with conflicting data exists")
    void createThrowIfEmployeeWithPhoneNumberExists(){
        String employeePhoneNumber = "999222111";
        User existingEmployee = new User();
        existingEmployee.setPhoneNumber(employeePhoneNumber);
        existingEmployee.setId(1);
        CreateEmployeeRequest employeeToAdd = new CreateEmployeeRequest(LocalDate.now(), "", "", Gender.FEMALE, LocalDate.now(), Country.CANADA, "", employeePhoneNumber, EmployeeRole.DOCTOR, "", "", "");

        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(existingEmployee));

        assertThrows(UserWithProvidedDataExists.class, () -> employeeService.createEmployee(employeeToAdd));
        verify(employeeMapper, never()).updateEmployeeFromRequest(any(), any());
    }

    @Test
    @DisplayName("Should not update employee when conflicting data exists")
    void UpdateThrowIfEmployeeWithPhoneNumberExists(){
        String employeePhoneNumber = "999222111";
        Employee existingEmployeeId1 = new Employee();
        existingEmployeeId1.setUser(new User());
        existingEmployeeId1.getUser().setPhoneNumber(employeePhoneNumber);
        existingEmployeeId1.getUser().setId(1);
        existingEmployeeId1.setId(1);
        Employee existingEmployeeId2 = new Employee();
        existingEmployeeId2.setUser(new User());
        existingEmployeeId2.getUser().setPhoneNumber(employeePhoneNumber);
        existingEmployeeId2.getUser().setId(2);
        existingEmployeeId2.setId(2);
        UpdateEmployeeRequest employeeToUpdate = new UpdateEmployeeRequest(LocalDate.now(), "", "", Gender.FEMALE, LocalDate.now(), Country.FRANCE, "", employeePhoneNumber, EmployeeRole.DOCTOR, "", "", "", true);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(existingEmployeeId1));
        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(existingEmployeeId2.getUser()));

        assertThrows(UserWithProvidedDataExists.class, () -> employeeService.updateEmployee(1, employeeToUpdate));
    }

    @Test
    @DisplayName("Should allow to update employee if user with such phone number or document ID or username exists and its id equals to the updating one")
    void updateTheSameEmployee(){
        String phoneNumber = "111222333";
        int id = 1;
        Employee mockedEmployee = new Employee();
        mockedEmployee.setUser(new User());
        mockedEmployee.getUser().setId(id);
        mockedEmployee.setId(id);
        mockedEmployee.getUser().setPhoneNumber(phoneNumber);
        UpdateEmployeeRequest mockEmployeeRequest = new UpdateEmployeeRequest(LocalDate.now(), "", "", Gender.FEMALE, LocalDate.now(), Country.FRANCE, "", phoneNumber, EmployeeRole.DOCTOR, "", "", "", true);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockedEmployee));
        when(userRepository.findAll(any(PredicateSpecification.class))).thenReturn(List.of(mockedEmployee.getUser()));

        assertDoesNotThrow(() -> employeeService.updateEmployee(id, mockEmployeeRequest));
        verify(employeeMapper).updateEmployeeFromRequest(mockEmployeeRequest ,mockedEmployee);
    }

    @Test
    @DisplayName("Should not return employee if no such employee exists")
    void throwOnGetEmployee(){
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployee(1));
    }

    @Test
    @DisplayName("Should not update employee if no such employee exists")
    void throwOnUpdateEmployee(){
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.updateEmployee(1, new UpdateEmployeeRequest(LocalDate.now(), "", "", Gender.FEMALE, LocalDate.now(), Country.FRANCE, "", "", EmployeeRole.DOCTOR, "", "", "", true)));
    }

    @Test
    @DisplayName("Should not delete employee if no such employee exists")
    void throwOnDeleteEmployee(){
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.deleteEmployee(1));
    }
}