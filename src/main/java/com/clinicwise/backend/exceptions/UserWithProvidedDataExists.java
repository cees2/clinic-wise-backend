package com.clinicwise.backend.exceptions;

public class UserWithProvidedDataExists extends RuntimeException {
    public UserWithProvidedDataExists(){
        super("An user with specified document ID, email or phone already exists");
    }
}
