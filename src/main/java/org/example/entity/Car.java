package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.client.AppUser;
import org.example.enums.CarStatus;
import org.example.enums.Currency;
import org.example.enums.Region;
import org.example.services.CarViewService;

@Data
@Builder
@Table(name = "cars")
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private CarBrand brand;
    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;
    @Min(60)
    @Max(560)
    private double power;
    private String imageUrl;
    private Double priceUAH;
    private Double priceUSD;
    private Double priceEUR;
    private Double originalPrice;
    private Double usdBuy;
    private Double usdSale;
    private Double eurBuy;
    private Double eurSale;
    private Double avgPriceRegionUAH;
    private Double avgPriceUkraineUAH;
    @Enumerated(EnumType.STRING)
    private Currency originalCurrency;
    private String phoneNumber;
    @Column(length = 3000)
    private String description;
    @Enumerated(EnumType.STRING)
    private CarStatus status;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;
}
