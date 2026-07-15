package com.yourstore.service;

import com.yourstore.dto.request.LoginRequest;
import com.yourstore.dto.request.RegisterRequest;
import com.yourstore.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String token);

    void requestPasswordReset(String email);

    void resetPassword(String token, String newPassword);
}