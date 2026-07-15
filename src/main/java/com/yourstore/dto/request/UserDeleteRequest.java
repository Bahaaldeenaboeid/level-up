package com.yourstore.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserDeleteRequest {

    @NotBlank(message = "Password is required to confirm account deletion")
    private String password;

    // Getters and Setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}