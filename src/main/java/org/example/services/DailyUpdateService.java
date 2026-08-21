package org.example.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyUpdateService {

    private final UpdateExchangeRatesService updateExchangeRatesService;
    private final UpdateCarsPriceService updateCarsPriceService;
    private final AveragePriceService averagePriceService;

    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Kyiv")
    public void dailyUpdate() {
        updateExchangeRatesService.updateExchangeRate();
        updateCarsPriceService.updateCarsPrice();
        averagePriceService.updateAllAveragePrices();
    }
}
