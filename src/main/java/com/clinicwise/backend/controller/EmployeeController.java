package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponse getEmployee(@PathVariable int employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequest) {
        return employeeService.createEmployee(createEmployeeRequest);
    }

    @PatchMapping("/{employeeId}")
    public EmployeeResponse updateEmployee(@PathVariable int employeeId, @RequestBody UpdateEmployeeRequest updateEmployeeRequest){
        return employeeService.updateEmployee(employeeId, updateEmployeeRequest);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int employeeId){
        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.noContent().build();
    }
}
