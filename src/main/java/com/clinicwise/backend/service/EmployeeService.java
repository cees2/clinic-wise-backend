package com.clinicwise.backend.service;

import com.clinicwise.backend.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    public List<Employee> getAllEmployees(){
        return List.of();
    }
}
