package org.example.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final AdminInitializer adminInitializer;
    private final CarBrandAndModelInitializer carBrandAndModelInitializer;

    public DataInitializer(AdminInitializer adminInitializer, CarBrandAndModelInitializer carBrandAndModelInitializer) {
        this.adminInitializer = adminInitializer;
        this.carBrandAndModelInitializer = carBrandAndModelInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        adminInitializer.init();
        carBrandAndModelInitializer.init();
    }
}
