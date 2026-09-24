package com.example.hostelflow.controller;

import com.example.hostelflow.dto.UserProvisionResponse;
import com.example.hostelflow.dto.UserRequest;
import com.example.hostelflow.dto.UserResponse;
import com.example.hostelflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserProvisionResponse createUser(
            @Valid @RequestBody UserRequest request) {

        return userService.provisionStudent(request);
    }

    @GetMapping("/{registrationNumber}")
    public UserResponse getUser(
            @PathVariable String registrationNumber) {

        return userService.getUserByRegistrationNumber(registrationNumber);
    }
}