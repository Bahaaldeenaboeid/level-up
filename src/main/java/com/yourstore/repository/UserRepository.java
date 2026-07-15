package com.yourstore.repository;

import com.yourstore.entity.User;
import com.yourstore.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used for login and authentication)
    Optional<User> findByEmail(String email);

    // Check if email already exists (used for registration validation)
    boolean existsByEmail(String email);

    // Find all users by role (e.g., find all ADMIN users)
    List<User> findByRole(Role role);

    // Find users by name containing keyword (for admin search)
    List<User> findByNameContainingIgnoreCase(String keyword);
}