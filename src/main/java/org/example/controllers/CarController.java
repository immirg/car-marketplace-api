package org.example.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.client.AppUser;
import org.example.dto.CarBrandDTO;
import org.example.dto.CarDTO;
import org.example.dto.CarModelDTO;
import org.example.dto.RequestAddingNewModelDTO;
import org.example.entity.*;
import org.example.services.CarService;
import org.example.views.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public String home() {
        return "home";
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getAllCars(@AuthenticationPrincipal AppUser appUser) {
        return carService.findAllCars(appUser);
    }

    @PostMapping("/cars")
    public ResponseEntity<String> addCarForReview(@AuthenticationPrincipal AppUser appUser, @RequestBody @Valid CarInReview carInReview) {
        return carService.addCarForReview(carInReview, appUser.getEmail());
    }

    @PatchMapping("/cars/reviews/current")
    public ResponseEntity<String> editCarForReview(@AuthenticationPrincipal AppUser appUser, @RequestBody @Valid CarInReview car) {
        return carService.editCarForReview(car, appUser.getEmail());
    }

    @GetMapping("/cars/reviews/current")
    public ResponseEntity<CarInReview> draftCarForReview(@AuthenticationPrincipal AppUser appUser) {
        return carService.draftCarForReview(appUser.getEmail());
    }

    @DeleteMapping("/cars/reviews/current")
    public ResponseEntity<Void> removeDraftCarAdding(@AuthenticationPrincipal AppUser appUser) {
        return carService.removeDraftCarAdding(appUser.getEmail());
    }

    @GetMapping("/cars/reviews/manager")
    public ResponseEntity<List<UnverifiedAds>> getCarsInReviews(@AuthenticationPrincipal AppUser appUser) {
        return carService.getCarsInReviews(appUser.getEmail());
    }

    @DeleteMapping("/cars/reviews/{id}")
    public ResponseEntity<Void> removeCarFromReviews(@PathVariable Integer id) {
        return carService.removeCarFromReview(id);
    }

    @GetMapping("/cars-in-review")
    public ResponseEntity<List<UnverifiedAds>> carsInReview() {
        return carService.carsForVerification();
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> removeCarById(@AuthenticationPrincipal AppUser appUser, @PathVariable Integer id) {
        return carService.removeCarById(appUser, id);
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/power/{value}")
    public ResponseEntity<List<CarDTO>> getCarByPower(@AuthenticationPrincipal AppUser appUser, @PathVariable double value) {
        return carService.findCarByPower(appUser, value);
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/producer/{brandId}")
    public ResponseEntity<List<CarDTO>> getCarByProducer(@AuthenticationPrincipal AppUser appUser, @PathVariable Integer brandId) {
        return carService.findByProducer(appUser, brandId);
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(@AuthenticationPrincipal AppUser appUser, @PathVariable Integer id) {
        return carService.findCarById(appUser, id);
    }

    @PostMapping("/cars/request-add-new-car")
    public ResponseEntity<Void> requestAddAbsentCar(@RequestBody RequestAddingNewModelDTO request) {
        return carService.requestAddAbsentCar(request);
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/get-my-active-cars")
    public ResponseEntity<List<CarDTO>> getMyActiveCars(@AuthenticationPrincipal AppUser appUser) {
        return carService.getMyActiveCars(appUser.getEmail());
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/get-my-sold-cars")
    public ResponseEntity<List<CarDTO>> getMySoldCars(@AuthenticationPrincipal AppUser appUser) {
        return carService.getMySoldCars(appUser.getEmail());
    }

    @PatchMapping("/car/{id}/sell")
    public ResponseEntity<Void> sellCar(@AuthenticationPrincipal AppUser appUser, @PathVariable Integer id) {
        return carService.sellCar(id, appUser.getEmail());
    }

    @GetMapping("/car-brands")
    public ResponseEntity<List<CarBrandDTO>> getAllBrands() {
        return carService.getAllBrands();
    }

    @GetMapping("/car-brands/{id}/models")
    public ResponseEntity<List<CarModelDTO>> getModelsForBrand(@PathVariable Integer id) {
        return carService.getModelsForBrand(id);
    }

    @PatchMapping("/cars/reviews/{id}/approve")
    public ResponseEntity<Void> approveCarAfterModeration(@PathVariable Integer id) {
        return carService.approveCarAfterModeration(id);
    }

    @PostMapping("/car/{id}/add-new-brand-and-model")
    public ResponseEntity<Void> addingNewBrandAndModel(@PathVariable Integer id) {
        return carService.addNewBrandAndModel(id);
    }

    @DeleteMapping("/car/{id}/remove-request-adding-new-brand-and-model")
    public ResponseEntity<Void> removeNewBrandAndModel(@PathVariable Integer id) {
        return carService.removeNewBrandAndModel(id);
    }

    @GetMapping("/car/get-all-requests-adding-new-brands-and-models")
    public ResponseEntity<List<RequestAddingNewModel>> getAllRequestNewBrandsAndModels() {
        return carService.getAllRequestNewBrandsAndModels();
    }

    @GetMapping("/regions")
    public ResponseEntity<List<String>> getAllAvailableRegions() {
        return carService.getAllAvailableRegions();
    }

    @JsonView(Views.User.class)
    @GetMapping("/cars/{region}/get-cars-for-region")
    public ResponseEntity<List<CarDTO>> getCarsForRegion(@AuthenticationPrincipal AppUser appUser, @PathVariable String region) {
        return carService.getCarsForRegion(appUser, region);
    }
}
