package org.example.config;

import org.example.dao.CarBrandDAO;
import org.example.dao.CarModelDAO;
import org.example.entity.CarBrand;
import org.example.entity.CarModel;
import org.springframework.stereotype.Component;

@Component
public class CarBrandAndModelInitializer {
    private final CarBrandDAO carBrandDAO;
    private final CarModelDAO carModelDAO;

    public CarBrandAndModelInitializer(CarBrandDAO carBrandDAO, CarModelDAO carModelDAO) {
        this.carBrandDAO = carBrandDAO;
        this.carModelDAO = carModelDAO;
    }

    public void init() {
        createBrandWithModels("BMW", "X5", "X6", "M3");
        createBrandWithModels("Audi", "A4", "A6", "Q7");
        createBrandWithModels("Daewoo", "Lanos");
    }

    private void createBrandWithModels(String brandName, String... modelNames) {
        CarBrand brand = carBrandDAO.findByName(brandName).orElseGet(() -> carBrandDAO.save(new CarBrand(brandName)));
        for (String modelName : modelNames) {
            if (!carModelDAO.existsByNameAndBrand(modelName, brand)) {
                carModelDAO.save(new CarModel(modelName, brand));
            }
        }
    }
}
