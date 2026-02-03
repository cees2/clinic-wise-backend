package com.clinicwise.backend.exceptions;

public class EmployeeWithProvidedDataExists extends RuntimeException {
    public EmployeeWithProvidedDataExists(String documentID){
        super("An user with specified document ID, email or phone already exists");
    }
}
