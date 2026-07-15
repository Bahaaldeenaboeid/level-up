package com.yourstore.service.impl;

import com.yourstore.core.exception.DuplicateResourceException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.entity.City;

import com.yourstore.repository.CityRepository;
import com.yourstore.service.CityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City createCity(String name, Double shippingRate) {
        if (cityRepository.existsByName(name)) {
            throw new DuplicateResourceException("City already exists: " + name);
        }

        City city = new City();
        city.setName(name);
        city.setShippingRate(shippingRate);
        city.setIsActive(true);

        return cityRepository.save(city);
    }

    @Override
    public City updateCity(Long cityId, String name, Double shippingRate) {
        City city = getCityById(cityId);

        if (name != null && !name.equals(city.getName()) && cityRepository.existsByName(name)) {
            throw new DuplicateResourceException("City already exists: " + name);
        }

        if (name != null) {
            city.setName(name);
        }
        if (shippingRate != null) {
            city.setShippingRate(shippingRate);
        }
        city.setUpdatedAt(LocalDateTime.now());

        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(Long cityId) {
        City city = getCityById(cityId);
        cityRepository.delete(city);
    }

    @Override
    public City getCityById(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + cityId));
    }

    @Override
    public City getCityByName(String name) {
        return cityRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + name));
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public List<City> getActiveCities() {
        return cityRepository.findByIsActiveTrue();
    }

    @Override
    public Double getShippingRateByCity(String cityName) {
        City city = getCityByName(cityName);
        return city.getShippingRate();
    }
}