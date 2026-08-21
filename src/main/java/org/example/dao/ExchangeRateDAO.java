package org.example.dao;

import org.example.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateDAO extends JpaRepository<ExchangeRate, Integer> {
    Optional<ExchangeRate> findFirstByOrderByIdDesc();
}
