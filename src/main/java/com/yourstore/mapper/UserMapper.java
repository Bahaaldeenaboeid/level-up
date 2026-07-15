package com.yourstore.mapper;

import com.yourstore.dto.request.RegisterRequest;
import com.yourstore.dto.request.UserProfileRequest;
import com.yourstore.dto.response.UserProfileResponse;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Will be encoded elsewhere
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return user;
    }

    public User toEntity(UserProfileRequest request, User existingUser) {
        if (request == null || existingUser == null) {
            return existingUser;
        }
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(request.getPassword()); // Will be encoded elsewhere
        }
        if (request.getPhone() != null) {
            existingUser.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            existingUser.setAddress(request.getAddress());
        }
        return existingUser;
    }

    public UserProfileResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}