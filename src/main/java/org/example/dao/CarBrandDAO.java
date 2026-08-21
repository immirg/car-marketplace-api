package org.example.dao;

import org.example.entity.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarBrandDAO extends JpaRepository<CarBrand, Integer> {
    Optional<CarBrand> findByName(String name);
    boolean existsByName(String name);
}
