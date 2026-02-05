package com.clinicwise.backend.exceptions;

public class PatientWithProvidedDataExists extends RuntimeException {
    public PatientWithProvidedDataExists() {
        super("A patient with specified document ID, email or phone already exists");;
    }
}
