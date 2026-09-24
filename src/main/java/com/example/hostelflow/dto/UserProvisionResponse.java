package com.example.hostelflow.dto;

public class UserProvisionResponse {

    private final UserResponse user;
    private final String activationCode;

    public UserProvisionResponse(UserResponse user, String activationCode) {
        this.user = user;
        this.activationCode = activationCode;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getActivationCode() {
        return activationCode;
    }
}
