package org.example.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.dao.ExchangeRateDAO;
import org.example.entity.ExchangeRate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateExchangeRatesService {
    private final CurrencyService currencyService;
    private final ExchangeRateDAO exchangeRateDAO;

    @PostConstruct
    public void initExchangeRate() {
        if (exchangeRateDAO.count() == 0) {
            updateExchangeRate();
        }
    }

    public void updateExchangeRate() {
        Map<String, Double> rates = currencyService.convert();
        ExchangeRate exchangeRate = exchangeRateDAO
                .findFirstByOrderByIdDesc()
                .orElseGet(ExchangeRate::new);

        exchangeRate.setUsdBuy(rates.get("usdBuy"));
        exchangeRate.setUsdSale(rates.get("usdSale"));
        exchangeRate.setEurBuy(rates.get("eurBuy"));
        exchangeRate.setEurSale(rates.get("eurSale"));
        exchangeRate.setUpdatedAt(LocalDateTime.now());

        exchangeRateDAO.save(exchangeRate);
    }
}
