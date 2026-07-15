package com.yourstore.mapper;

import com.yourstore.dto.request.RegisterRequest;
import com.yourstore.dto.response.AuthResponse;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return user;
    }

    public AuthResponse toResponse(User user, String token) {
        if (user == null) {
            return null;
        }
        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getName(),
                user.getEmail()
        );
    }
}