package com.clinicwise.backend.exceptions;

public class EmployeeWithProvidedDataExists extends RuntimeException {
    public EmployeeWithProvidedDataExists(){
        super("An user with specified document ID, email or phone already exists");
    }
}
