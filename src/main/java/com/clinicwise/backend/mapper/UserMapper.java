package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserMapper {
    public static UserResponse toResponse(User user) {
        Set<String> authorities = user.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority().name())
                .collect(Collectors.toSet());

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
                user.getPhoneNumber(),
                authorities
        );
    }
}
