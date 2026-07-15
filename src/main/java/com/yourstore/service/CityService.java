package com.yourstore.service;

import com.yourstore.entity.City;

import java.util.List;

public interface CityService {

    City createCity(String name, Double shippingRate);

    City updateCity(Long cityId, String name, Double shippingRate);

    void deleteCity(Long cityId);

    City getCityById(Long cityId);

    City getCityByName(String name);

    List<City> getAllCities();

    List<City> getActiveCities();

    Double getShippingRateByCity(String cityName);
}