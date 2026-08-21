package org.example.dao;

import org.example.client.AppUser;
import org.example.entity.Car;
import org.example.entity.CarBrand;
import org.example.entity.CarModel;
import org.example.enums.CarStatus;
import org.example.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarDAO extends JpaRepository<Car, Integer> {
    List<Car> findByPower(double power);
    List<Car> findByRegion(Region region);
    List<Car> findByBrand_Id(Integer brandId);
    List<Car> findByPowerLessThan(double power);
    List<Car> findByOwnerAndStatus(AppUser owner, CarStatus status);
    List<Car> findAllByStatus(CarStatus status);
    List<Car> findAllByBrandAndModelAndRegionAndStatus(CarBrand brand, CarModel model, Region region, CarStatus status);
    List<Car> findAllByBrandAndModelAndStatus(CarBrand brand, CarModel model, CarStatus status);

//    @Query("select * from For_verification")
//    List<UnverifiedAds> allCarsWaitingForReview();
//    @Query("delete from For_verification fv where fv.id =:id")
//    void removeCarFromWaitingForReview(int id);
}
