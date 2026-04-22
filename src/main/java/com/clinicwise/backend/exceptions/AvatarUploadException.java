package com.clinicwise.backend.exceptions;

public class AvatarUploadException extends RuntimeException {
    public AvatarUploadException(String fileName) {
        super("Could not upload avatar file: " + fileName);
    }
}
