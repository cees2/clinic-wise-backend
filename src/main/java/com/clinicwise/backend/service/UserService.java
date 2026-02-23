package com.clinicwise.backend.service;

import com.clinicwise.backend.dto.request.LoginRequest;
import com.clinicwise.backend.dto.response.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private AuthenticationManager authenticationManager;
    private JWTService JWTService;

    public UserService(AuthenticationManager authenticationManager, JWTService JWTService){
        this.authenticationManager = authenticationManager;
        this.JWTService = JWTService;
    }

    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        if(authentication.isAuthenticated()){
            String token = JWTService.generateToken(loginRequest.username());
            return new LoginResponse(token);
        }

        throw new BadCredentialsException("Invalid username or password");
    }
}
