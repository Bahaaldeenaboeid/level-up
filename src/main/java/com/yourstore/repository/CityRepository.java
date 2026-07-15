package com.yourstore.repository;

import com.yourstore.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Find city by name (for validation and shipping calculation)
    Optional<City> findByName(String name);

    // Find all active cities (for shipping dropdown)
    List<City> findByIsActiveTrue();

    // Check if city name exists
    boolean existsByName(String name);
}