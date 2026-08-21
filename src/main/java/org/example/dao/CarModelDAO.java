package org.example.dao;

import org.example.entity.CarBrand;
import org.example.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarModelDAO extends JpaRepository<CarModel, Integer> {
    List<CarModel> findByName(String name);
    List<CarModel> findByBrand_Id(Integer brandId);
    Optional<CarModel> findByNameAndBrand(String name, CarBrand brand);
    boolean existsByNameAndBrand(String name, CarBrand brand);
}
