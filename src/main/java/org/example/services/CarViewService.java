package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dao.CarDAO;
import org.example.dao.CarViewDAO;
import org.example.entity.Car;
import org.example.entity.CarView;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class CarViewService {
    private final CarDAO carDAO;
    private final CarViewDAO carViewDAO;

    public void addView(Car car) {
        CarView carView = new CarView();
        carView.setCar(car);
        carView.setViewedAt(LocalDateTime.now());

        carViewDAO.save(carView);
    }

    public long getAllViews(Integer carId) {
        return carViewDAO.countByCar_id(carId);
    }

    public long getDailyViews(Integer carId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return carViewDAO.countByCar_idAndViewedAtGreaterThanEqual(carId, startOfDay);
    }

    public long getWeeklyViews(Integer carId) {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        return carViewDAO.countByCar_idAndViewedAtGreaterThanEqual(carId, startOfWeek);
    }

    public long getMonthlyViews(Integer carId) {
        LocalDateTime startOfMonth = LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
        return carViewDAO.countByCar_idAndViewedAtGreaterThanEqual(carId, startOfMonth);
    }
}
