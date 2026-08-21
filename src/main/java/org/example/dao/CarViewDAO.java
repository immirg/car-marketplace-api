package org.example.dao;

import org.example.entity.CarView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CarViewDAO extends JpaRepository<CarView, Integer> {
    long countByCar_id(Integer carId);
    long countByCar_idAndViewedAtGreaterThanEqual(Integer carId, LocalDateTime date);
}
