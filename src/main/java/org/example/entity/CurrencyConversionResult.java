package org.example.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConversionResult {

    private Double priceUAH;
    private Double priceUSD;
    private Double priceEUR;

    private Double usdBuy;
    private Double usdSale;
    private Double eurBuy;
    private Double eurSale;
}
