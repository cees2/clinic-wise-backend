package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.LoginRequest;
import com.clinicwise.backend.dto.response.LoginResponse;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.service.AuthService;
import com.clinicwise.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @GetMapping("/me")
    public UserResponse me(@RequestHeader("Authorization") String authorizationHeader){
        return authService.getMe(authorizationHeader);
    }
}
