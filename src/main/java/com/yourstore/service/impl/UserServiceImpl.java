package com.yourstore.service.impl;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.dto.request.UserDeleteRequest;
import com.yourstore.dto.request.UserProfileRequest;
import com.yourstore.dto.response.UserProfileResponse;
import com.yourstore.entity.User;
import com.yourstore.mapper.UserMapper;
import com.yourstore.repository.UserRepository;
import com.yourstore.service.EmailService;
import com.yourstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = findById(userId);
        return userMapper.toResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {
        User user = findById(userId);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteAccount(Long userId, UserDeleteRequest request) {
        User user = findById(userId);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("Invalid password");
        }

        emailService.sendAccountDeletedEmail(user.getEmail(), user.getName());
        userRepository.delete(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    // ✅ ADD THIS METHOD
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}