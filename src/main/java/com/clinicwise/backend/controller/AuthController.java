package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.LoginRequest;
import com.clinicwise.backend.dto.response.LoginResponse;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return userService.login(loginRequest);
    }
}
