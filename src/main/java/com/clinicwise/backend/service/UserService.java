package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.LoginRequest;
import com.clinicwise.backend.dto.response.LoginResponse;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.mapper.UserMapper;
import com.clinicwise.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private AuthenticationManager authenticationManager;
    private JWTService JWTService;
    private UserRepository userRepository;

    public UserService(AuthenticationManager authenticationManager, JWTService JWTService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.JWTService = JWTService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(loginRequest.email())
                    .orElseThrow(() -> new UsernameNotFoundException(loginRequest.email()));
            String token = JWTService.generateToken(loginRequest.email());
            UserResponse userResponse = UserMapper.toResponse(user);

            return new LoginResponse(token, userResponse);
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    public UserResponse getMe(String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());

        String usernameFromToken = JWTService.extractUsername(token);

        User user = userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return UserMapper.toResponse(user);
    }
}
