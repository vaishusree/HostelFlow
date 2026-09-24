package com.example.hostelflow.controller;

import com.example.hostelflow.dto.ActivateRequest;
import com.example.hostelflow.dto.LoginRequest;
import com.example.hostelflow.dto.LoginResponse;
import com.example.hostelflow.dto.UserResponse;
import com.example.hostelflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/activate")
    public UserResponse activate(
            @Valid @RequestBody ActivateRequest request) {

        return userService.activateStudent(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}