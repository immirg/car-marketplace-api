package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dao.ExchangeRateDAO;
import org.example.entity.CurrencyConversionResult;
import org.example.entity.ExchangeRate;
import org.example.enums.Currency;
import org.example.exceptions.UserException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {
    private final ExchangeRateDAO exchangeRateDAO;

    public CurrencyConversionResult convert(Currency originalCurrency, Double originalPrice) {
        if (originalCurrency == null || originalPrice == null) {
            throw new UserException("Original price and currency are required");
        }
        ExchangeRate exchangeRate = exchangeRateDAO
                .findFirstByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("Exchange rate not found"));

        Double usdBuy = exchangeRate.getUsdBuy();
        Double usdSale = exchangeRate.getUsdSale();
        Double eurBuy = exchangeRate.getEurBuy();
        Double eurSale = exchangeRate.getEurSale();

        CurrencyConversionResult result =
                CurrencyConversionResult.builder()
                        .usdBuy(usdBuy)
                        .usdSale(usdSale)
                        .eurBuy(eurBuy)
                        .eurSale(eurSale)
                        .build();

        if (originalCurrency == Currency.UAH) {
            result.setPriceUAH(originalPrice);
            result.setPriceUSD(originalPrice / usdSale);
            result.setPriceEUR(originalPrice / eurSale);
        }
        if (originalCurrency == Currency.USD) {
            result.setPriceUSD(originalPrice);
            result.setPriceUAH(originalPrice * usdBuy);
            result.setPriceEUR(originalPrice * usdBuy / eurSale);
        }
        if (originalCurrency == Currency.EUR) {
            result.setPriceEUR(originalPrice);
            result.setPriceUAH(originalPrice * eurBuy);
            result.setPriceUSD(originalPrice * eurBuy / usdSale);
        }
        return result;
    }
}
