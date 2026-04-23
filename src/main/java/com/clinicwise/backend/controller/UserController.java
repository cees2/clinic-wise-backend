package com.clinicwise.backend.controller;

import com.clinicwise.backend.dto.request.UserRequest;
import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping(value = "/me", consumes = "multipart/form-data", produces = "application/json")
    public UserResponse updateUser(@RequestPart("user") UserRequest userRequest,
                                   @RequestPart(name = "avatar", required = false) MultipartFile avatar,
                                   HttpServletRequest request) {
        return userService.updateUser(userRequest, avatar, request);
    }
}
