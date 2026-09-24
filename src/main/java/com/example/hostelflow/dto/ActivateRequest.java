package com.example.hostelflow.dto;

import jakarta.validation.constraints.NotBlank;

public class ActivateRequest {

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String activationCode;

    @NotBlank
    private String newPassword;

    public ActivateRequest() {
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}