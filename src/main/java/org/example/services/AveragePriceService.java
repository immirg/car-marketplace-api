package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dao.CarDAO;
import org.example.entity.Car;
import org.example.entity.CarBrand;
import org.example.entity.CarModel;
import org.example.enums.CarStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AveragePriceService {
    private final CarDAO carDAO;
    public void updateAveragePriceForGroup(CarBrand carBrand, CarModel carModel) {
        List<Car> cars = carDAO.findAllByBrandAndModelAndStatus(carBrand, carModel, CarStatus.ACTIVE);

        double avgPriceUkraine = cars.stream()
                .mapToDouble(Car::getPriceUAH)
                .average()
                .orElse(0);

        for (Car car: cars) {
            double avgPriceRegion = cars.stream()
                    .filter(c -> c.getRegion() == car.getRegion())
                    .mapToDouble(Car::getPriceUAH)
                    .average()
                    .orElse(0);

            car.setAvgPriceUkraineUAH(avgPriceUkraine);
            car.setAvgPriceRegionUAH(avgPriceRegion);
        }
        carDAO.saveAll(cars);
    }

    public void updateAllAveragePrices() {
        List<Car> cars = carDAO.findAllByStatus(CarStatus.ACTIVE);
        Set<String> processGroup = new HashSet<>();
        for (Car car: cars) {
            String groupKey = car.getBrand().getId() + "_" + car.getModel().getId();

            if (processGroup.add(groupKey)) {
                updateAveragePriceForGroup(car.getBrand(), car.getModel());
            }
        }
    }
}
