package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.enums.Currency;
import org.example.enums.Region;

@Data
@Builder
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CarInReview {
    @NotNull
    private Integer modelId;
    @NotNull
    private Integer brandId;
    @Min(60)
    @Max(560)
    private double power;
    private String imageUrl;
    private Double originalPrice;
    @Enumerated(EnumType.STRING)
    private Currency originalCurrency;
    private String phoneNumber;
    @Column(length = 3000)
    private String description;
    @NotNull(message = "region is required")
    @Enumerated(EnumType.STRING)
    private Region region;
}
