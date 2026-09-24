package com.example.hostelflow.dto;

import com.example.hostelflow.model.UserRole;

import java.time.LocalDateTime;

public class UserResponse{
    private Long id;
    private String registrationNumber;
    private String name;
    private String email;
    private boolean activated;
    private UserRole userRole;
    private LocalDateTime createdAt;

    public UserResponse(Long id, String registrationNumber, String name, String email, boolean activated, UserRole userRole, LocalDateTime createdAt) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.email = email;
        this.activated = activated;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
