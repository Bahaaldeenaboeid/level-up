package com.yourstore.mapper;

import com.yourstore.entity.City;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CityMapper {

    public CityResponse toResponse(City city) {
        if (city == null) {
            return null;
        }
        CityResponse response = new CityResponse();
        response.setId(city.getId());
        response.setName(city.getName());
        response.setShippingRate(city.getShippingRate());
        response.setIsActive(city.getIsActive());
        response.setCreatedAt(city.getCreatedAt());
        response.setUpdatedAt(city.getUpdatedAt());
        return response;
    }

    public List<CityResponse> toResponseList(List<City> cities) {
        if (cities == null) {
            return new ArrayList<>();
        }
        return cities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Inner class for City response
    public static class CityResponse {
        private Long id;
        private String name;
        private Double shippingRate;
        private Boolean isActive;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getShippingRate() { return shippingRate; }
        public void setShippingRate(Double shippingRate) { this.shippingRate = shippingRate; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }

        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}