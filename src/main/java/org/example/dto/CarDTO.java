package org.example.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.client.AppUser;
import org.example.entity.CarBrand;
import org.example.entity.CarModel;
import org.example.enums.CarStatus;
import org.example.enums.Currency;
import org.example.enums.Region;
import org.example.views.Views;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {

    @JsonView(Views.User.class)
    private Integer carId;

    @JsonView(Views.User.class)
    private String model;

    @JsonView(Views.User.class)
    private String brand;

    @JsonView(Views.User.class)
    private double power;

    @JsonView(Views.User.class)
    private String imageUrl;

    @JsonView(Views.User.class)
    private Double priceUAH;

    @JsonView(Views.User.class)
    private Double priceUSD;

    @JsonView(Views.User.class)
    private Double priceEUR;

    @JsonView(Views.User.class)
    private Double originalPrice;

    @JsonView(Views.User.class)
    private Double usdBuy;

    @JsonView(Views.User.class)
    private Double usdSale;

    @JsonView(Views.User.class)
    private Double eurBuy;

    @JsonView(Views.User.class)
    private Double eurSale;

    @JsonView(Views.User.class)
    private Currency originalCurrency;

    @JsonView(Views.User.class)
    private String phoneNumber;

    @JsonView(Views.User.class)
    private Double avgPriceRegionUAH;

    @JsonView(Views.User.class)
    private Double avgPriceUkraineUAH;

    @JsonView(Views.User.class)
    private Long allViews;

    @JsonView(Views.User.class)
    private Long dailyViews;

    @JsonView(Views.User.class)
    private Long weeklyViews;

    @JsonView(Views.User.class)
    private Long monthlyViews;

    @JsonView(Views.User.class)
    private String description;

    @JsonView(Views.User.class)
    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @JsonView(Views.User.class)
    @Enumerated(EnumType.STRING)
    private Region region;
}
