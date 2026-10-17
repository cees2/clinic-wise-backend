package com.clinicwise.backend.controller;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.common.list.sort.SearchSelectBaseFilter;
import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
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
    public PageResponse<EmployeeResponse> getAllEmployees(@ModelAttribute EmployeesFilter filter) {
        return employeeService.getAllEmployees(filter);
    }

    @GetMapping("/{employeeId}")
    public ApiResponse<EmployeeResponse> getEmployee(@PathVariable int employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping
    public ApiResponse<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequest) {
        return employeeService.createEmployee(createEmployeeRequest);
    }

    @PatchMapping("/{employeeId}")
    public ApiResponse<EmployeeResponse> updateEmployee(@PathVariable int employeeId, @RequestBody UpdateEmployeeRequest updateEmployeeRequest){
        return employeeService.updateEmployee(employeeId, updateEmployeeRequest);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int employeeId){
        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search_select")
    public ApiResponse<PageResponse<SearchSelect>> getSearchSelect(@ModelAttribute SearchSelectBaseFilter searchSelectBaseFilter){
        return employeeService.getSearchSelect(searchSelectBaseFilter);
    }

    @PostMapping("/generate")
    public ApiResponse<List<EmployeeResponse>> generate(){
        return employeeService.generateEmployees();
    }
}
