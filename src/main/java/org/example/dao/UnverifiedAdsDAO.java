package org.example.dao;

import org.example.entity.UnverifiedAds;
import org.example.enums.AdReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnverifiedAdsDAO extends JpaRepository<UnverifiedAds, Integer> {
    void deleteById(Integer id);
    boolean existsByUserEmail(String userEmail);
    Optional<UnverifiedAds> findByUserEmail(String userEmail);
    Optional<UnverifiedAds> findByUserEmailAndStatus(String userEmail, AdReviewStatus status);
    List<UnverifiedAds> findAllByStatus(AdReviewStatus status);
    boolean existsByUserEmailAndStatus(String userEmail, AdReviewStatus status);
}
