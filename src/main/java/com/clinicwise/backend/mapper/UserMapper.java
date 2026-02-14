package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getGender(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getDocumentId(),
                user.getEmail(),
                user.getEnabled(),
                user.getNationality(),
                user.getPhoneNumber()
        );
    }
}
