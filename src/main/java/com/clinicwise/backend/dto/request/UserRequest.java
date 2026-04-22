package com.clinicwise.backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.sql.Blob;

public record UserRequest(
        @NotNull
        String username,
        @NotNull
        String firstname,
        @NotNull
        String lastname
) {
}
