package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.AdReviewStatus;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "for_verification")
public class UnverifiedAds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userEmail;
    @Embedded
    private CarInReview carInReview;
    @Column(nullable = false)
    private int editAttempt = 0;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdReviewStatus status = AdReviewStatus.NEEDS_EDIT;

    public UnverifiedAds(String userEmail, CarInReview carInReview) {
        this.userEmail = userEmail;
        this.carInReview = carInReview;
    }
}
