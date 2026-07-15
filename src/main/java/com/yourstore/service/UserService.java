package com.yourstore.service;  // ← ADD THIS

import com.yourstore.dto.request.UserDeleteRequest;
import com.yourstore.dto.request.UserProfileRequest;
import com.yourstore.dto.response.UserProfileResponse;
import com.yourstore.entity.User;

public interface UserService {

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UserProfileRequest request);

    void deleteAccount(Long userId, UserDeleteRequest request);

    User findById(Long userId);

    User findByEmail(String email);  // ← ADD THIS

    boolean existsByEmail(String email);
}