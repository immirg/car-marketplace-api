package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dao.CarDAO;
import org.example.dao.ExchangeRateDAO;
import org.example.entity.Car;
import org.example.entity.CurrencyConversionResult;
import org.example.enums.CarStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCarsPriceService {
    private final CarDAO carDAO;
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyConversionService currencyConversionService;

    public void updateCarsPrice() {
        List<Car> cars = carDAO.findAllByStatus(CarStatus.ACTIVE);
        cars.forEach(car -> {
            CurrencyConversionResult result = currencyConversionService.convert(
                    car.getOriginalCurrency(),
                    car.getOriginalPrice()
            );
            car.setPriceUAH(result.getPriceUAH());
            car.setPriceUSD(result.getPriceUSD());
            car.setPriceEUR(result.getPriceEUR());

            car.setUsdBuy(result.getUsdBuy());
            car.setUsdSale(result.getUsdSale());
            car.setEurBuy(result.getEurBuy());
            car.setEurSale(result.getEurSale());
        });
        carDAO.saveAll(cars);
    }
}
