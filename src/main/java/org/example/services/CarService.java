package org.example.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.example.client.AppUser;
import org.example.dao.*;
import org.example.dto.CarBrandDTO;
import org.example.dto.CarDTO;
import org.example.dto.CarModelDTO;
import org.example.dto.RequestAddingNewModelDTO;
import org.example.entity.*;
import org.example.enums.*;
import org.example.exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarDAO carDAO;
    private final CarBrandDAO carBrandDAO;
    private final CarModelDAO carModelDAO;
    private final AppUserDAO appUserDAO;
    private final ExchangeRateDAO exchangeRateDAO;
    private final UnverifiedAdsDAO unverifiedAdsDAO;
    private final CurrencyService currencyService;
    private final ProfanityService profanityService;
    private final MailService mailService;
    private final CurrencyConversionService currencyConversionService;
    private final AveragePriceService averagePriceService;
    private final CarViewService carViewService;
    private final RequestAddingNewModelDAO requestAddingNewModelDAO;

    public ResponseEntity<List<CarDTO>> findAllCars(AppUser appUser) {
        List<CarDTO> cars;
        cars = carDAO.findAll().stream().filter(c -> c.getStatus().equals(CarStatus.ACTIVE)).map(car ->  getCarInfo(car, appUser)).toList();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    private CarDTO getCarInfo(Car car, AppUser appUser) {
        CarDTO newCar = CarDTO.builder()
                .carId(car.getId())
                .model(car.getModel().getName())
                .brand(car.getBrand().getName())
                .power(car.getPower())
                .imageUrl(car.getImageUrl())
                .priceUAH(car.getPriceUAH())
                .priceUSD(car.getPriceUSD())
                .priceEUR(car.getPriceEUR())
                .originalPrice(car.getOriginalPrice())
                .originalCurrency(car.getOriginalCurrency())
                .usdBuy(car.getUsdBuy())
                .usdSale(car.getUsdSale())
                .eurBuy(car.getEurBuy())
                .eurSale(car.getEurSale())
                .phoneNumber(car.getPhoneNumber())
                .description(car.getDescription())
                .region(car.getRegion())
                .status(car.getStatus())
                .build();

        if (appUser!= null && appUser.getAccountType().equals(AccountType.PREMIUM)) {
            newCar.setAvgPriceRegionUAH(car.getAvgPriceRegionUAH());
            newCar.setAvgPriceUkraineUAH(car.getAvgPriceUkraineUAH());
            newCar.setAllViews(carViewService.getAllViews(car.getId()));
            newCar.setDailyViews(carViewService.getDailyViews(car.getId()));
            newCar.setWeeklyViews(carViewService.getWeeklyViews(car.getId()));
            newCar.setMonthlyViews(carViewService.getMonthlyViews(car.getId()));
        }
        return newCar;
    }

    public ResponseEntity<CarDTO> findCarById(AppUser appUser, Integer id) {
        Car car = carDAO.findById(id).orElseThrow(() -> new UserException("Car not found"));
        carViewService.addView(car);
        CarDTO carDTO = getCarInfo(car, appUser);
        return new ResponseEntity<>(carDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<CarDTO>> findCarByPower(AppUser appUser, double power) {
        List<Car> cars = carDAO.findByPower(power);
        if (cars.isEmpty()) {
            throw new UserException("Cars not found");
        }
        List<CarDTO> carsDTO = new ArrayList<>();
        for (Car car: cars) {
            CarDTO carDTO = getCarInfo(car, appUser);
            carsDTO.add(carDTO);
        }
        return new ResponseEntity<>(carsDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<CarDTO>> findByProducer(AppUser appUser, Integer brandId) {
        List<Car> cars = carDAO.findByBrand_Id(brandId);
        if (cars.isEmpty()) {
            throw new UserException("Cars not found");
        }
        List<CarDTO> carsDTO = new ArrayList<>();
        for (Car car: cars) {
            CarDTO carDTO = getCarInfo(car, appUser);
            carsDTO.add(carDTO);
        }
        return new ResponseEntity<>(carsDTO, HttpStatus.OK);
    }

    public ResponseEntity<Void> removeCarById(AppUser appUser, Integer id) {
        Car car = carDAO.findById(id).orElseThrow(() -> new UserException("Car not found"));

        if (!appUser.getEmail().equals(car.getOwner().getEmail()) &&
                !appUser.getRole().name().equals(Role.PLATFORM_ADMIN.name()) &&
                !appUser.getRole().name().equals(Role.PLATFORM_MANAGER.name())) {
            throw new UserException("you can't remove this add");
        }
        CarBrand carBrand = car.getBrand();
        CarModel carModel = car.getModel();

        carDAO.deleteById(id);
        averagePriceService.updateAveragePriceForGroup(carBrand, carModel);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> removeCarFromReview(int unverifiedAdsId) {
        unverifiedAdsDAO.deleteById(unverifiedAdsId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<String> addNewCar(Car car, int unverifiedAdsId, AppUser appUser) {
        carDAO.save(car);
        averagePriceService.updateAveragePriceForGroup(car.getBrand(), car.getModel());
        removeCarFromReview(unverifiedAdsId);

        if (appUser.getRole() == Role.USER) {
            appUser.setRole(Role.SELLER);
            appUserDAO.save(appUser);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<UnverifiedAds>> carsForVerification() {
        List<UnverifiedAds> ads = unverifiedAdsDAO.findAll();
        if (ads.isEmpty()) {
            throw new UserException("Cars not found");
        }
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    public ResponseEntity<String> addCarForReview(CarInReview carInReview, String userEmail) {
        AppUser appUser = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));
        validateCarData(carInReview);

        if (unverifiedAdsDAO.existsByUserEmailAndStatus(userEmail, AdReviewStatus.NEEDS_EDIT)) {
            return ResponseEntity
                    .status(409)
                    .body("Finish editing the existing ad before creating a new one");
        }

        UnverifiedAds unverifiedAds = new UnverifiedAds(userEmail, carInReview);
        unverifiedAdsDAO.save(unverifiedAds);
        int unverifiedAdsId = unverifiedAds.getId();

        if (profanityService.containsForbiddenWords(carInReview.getDescription())) {
            unverifiedAds.setEditAttempt(unverifiedAds.getEditAttempt() + 1);
            unverifiedAdsDAO.save(unverifiedAds);
            return ResponseEntity
                    .status(422)
                    .header("unverifiedAdsId", String.valueOf(unverifiedAdsId))
                    .body("Description contains forbidden words");
        }
        checkActiveAdsLimits(appUser);
        Car car = carBuilder(carInReview, appUser);
        return addNewCar(car, unverifiedAdsId, appUser);
    }

    public ResponseEntity<String> editCarForReview(CarInReview carInReview, String userEmail) {
        UnverifiedAds unverifiedAds = unverifiedAdsDAO
                .findByUserEmailAndStatus(userEmail, AdReviewStatus.NEEDS_EDIT)
                .orElseThrow(() -> new UserException("Ad not found"));

//        UnverifiedAds unverifiedAds = unverifiedAdsDAO.findById(id).orElseThrow(() -> new UserException("Ad not found"));
        validateCarData(carInReview);

        if (!unverifiedAds.getUserEmail().equals(userEmail)) {
            throw new UserException("You cannot edit this ad");
        }

        if (profanityService.containsForbiddenWords(carInReview.getDescription())) {
            int newAttempt = unverifiedAds.getEditAttempt() + 1;
            unverifiedAds.setEditAttempt(newAttempt);
            unverifiedAds.setCarInReview(carInReview);
            unverifiedAdsDAO.save(unverifiedAds);

            if (newAttempt >= 3) {
                unverifiedAds.setStatus(AdReviewStatus.WAITING_FOR_MANAGER);
                unverifiedAdsDAO.save(unverifiedAds);
                mailService.send(carInReview, userEmail);
                throw new UserException("This ad can't be added");
            }

            int unverifiedAdsId = unverifiedAds.getId();
            return ResponseEntity
                    .status(422)
                    .header("unverifiedAdsId", String.valueOf(unverifiedAdsId))
                    .body("Description contains forbidden words");
        }
        AppUser appUser = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));
        checkActiveAdsLimits(appUser);
        Car car = carBuilder(carInReview, appUser);
        return addNewCar(car, unverifiedAds.getId(), appUser);
    }

    private Car carBuilder(CarInReview carInReview, AppUser appUser) {
        CurrencyConversionResult currencyConversion = currencyConversionService.convert(carInReview.getOriginalCurrency(), carInReview.getOriginalPrice());
        CarBrand brand = getBrand(carInReview.getBrandId());
        CarModel model = getModel(carInReview.getModelId(), brand);
        return Car.builder()
                .power(carInReview.getPower())
                .brand(brand)
                .model(model)
                .imageUrl(carInReview.getImageUrl())
                .priceUAH(currencyConversion.getPriceUAH())
                .priceUSD(currencyConversion.getPriceUSD())
                .priceEUR(currencyConversion.getPriceEUR())
                .originalPrice(carInReview.getOriginalPrice())
                .usdBuy(currencyConversion.getUsdBuy())
                .usdSale(currencyConversion.getUsdSale())
                .eurBuy(currencyConversion.getEurBuy())
                .eurSale(currencyConversion.getEurSale())
                .originalCurrency(carInReview.getOriginalCurrency())
                .phoneNumber(carInReview.getPhoneNumber())
                .description(carInReview.getDescription())
                .region(carInReview.getRegion())
                .owner(appUser)
                .status(CarStatus.ACTIVE)
                .build();
    }

    public ResponseEntity<CarInReview> draftCarForReview(String userEmail) {
        appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));
        return unverifiedAdsDAO
                .findByUserEmailAndStatus(userEmail, AdReviewStatus.NEEDS_EDIT)
                .map(unverifiedAds -> ResponseEntity.ok(unverifiedAds.getCarInReview()))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    public ResponseEntity<Void> removeDraftCarAdding(String userEmail) {
        UnverifiedAds unverifiedAds = unverifiedAdsDAO
                .findByUserEmailAndStatus(userEmail, AdReviewStatus.NEEDS_EDIT)
                .orElseThrow(() -> new UserException("Draft not found"));
        unverifiedAdsDAO.delete(unverifiedAds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<UnverifiedAds>> getCarsInReviews(String userEmail) {
        AppUser appUser = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));
        if (appUser.getRole() != Role.PLATFORM_ADMIN && appUser.getRole() != Role.PLATFORM_MANAGER) {
            throw new UserException("You don't have permission");
        }
        List<UnverifiedAds> carsInReview = unverifiedAdsDAO.findAllByStatus(AdReviewStatus.WAITING_FOR_MANAGER);
        return new ResponseEntity<>(carsInReview, HttpStatus.OK);
    }

    public ResponseEntity<Void> requestAddAbsentCar(RequestAddingNewModelDTO request) {
        RequestAddingNewModel requestNewModel = new RequestAddingNewModel(request.getProducer(), request.getModel());
        requestAddingNewModelDAO.save(requestNewModel);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<CarDTO>> getMyActiveCars(String userEmail) {
        List<CarDTO> carsDTO = getUserCars(userEmail, CarStatus.ACTIVE);
        return new ResponseEntity<>(carsDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<CarDTO>> getMySoldCars(String userEmail) {
        List<CarDTO> carsDTO = getUserCars(userEmail, CarStatus.SOLD);
        return new ResponseEntity<>(carsDTO, HttpStatus.OK);
    }

    private List<CarDTO> getUserCars(String userEmail, CarStatus status) {
        AppUser appUser = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));
        List<Car> cars = carDAO.findByOwnerAndStatus(appUser, status);
        List<CarDTO> carsDTO = new ArrayList<>();
        for (Car car: cars) {
            carsDTO.add(getCarInfo(car, appUser));
        }
        return carsDTO;
    }

    public ResponseEntity<Void> sellCar(Integer id, String userEmail) {
        Car car = carDAO.findById(id).orElseThrow(() -> new UserException("Car not found"));
        AppUser owner = appUserDAO.findAppUserByEmail(userEmail).orElseThrow(() -> new UserException("user not found"));

        if (car.getOwner().getId() != owner.getId()) {
            throw new UserException("You can't sell this car");
        }
        car.setStatus(CarStatus.SOLD);
        carDAO.save(car);
        averagePriceService.updateAveragePriceForGroup(car.getBrand(), car.getModel());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<CarBrandDTO>> getAllBrands() {
        List<CarBrandDTO> carBrands = carBrandDAO.findAll()
                .stream()
                .map(brand -> new CarBrandDTO(
                        brand.getId(),
                        brand.getName()
                ))
                .toList();
        return new ResponseEntity<>(carBrands, HttpStatus.OK);
    }

    public ResponseEntity<List<CarModelDTO>> getModelsForBrand(Integer id) {
        List<CarModelDTO> models = carModelDAO.findByBrand_Id(id)
                .stream()
                .map(model -> new CarModelDTO(model.getBrand().getName(), model.getName(), model.getId()))
                .toList();
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    public ResponseEntity<Void> approveCarAfterModeration(Integer id) {
        UnverifiedAds unverifiedAds = unverifiedAdsDAO.findById(id).orElseThrow(() -> new UserException("Ad not found"));

        if (unverifiedAds.getStatus() != AdReviewStatus.WAITING_FOR_MANAGER) {
            throw new UserException("Ad is not waiting for manager moderation");
        }

        AppUser appUser = appUserDAO.findAppUserByEmail(unverifiedAds.getUserEmail()).orElseThrow(() -> new UserException("user not found"));
        checkActiveAdsLimits(appUser);
        Car car = carBuilder(unverifiedAds.getCarInReview(), appUser);
        addNewCar(car, unverifiedAds.getId(), appUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkActiveAdsLimits(AppUser appUser) {
        int activeAd = carDAO.findByOwnerAndStatus(appUser, CarStatus.ACTIVE).size();
        if (appUser.getAccountType().equals(AccountType.BASIC) && activeAd >= 1) {
            throw new UserException("Free ad limit reached");
        }
    }

    public ResponseEntity<Void> addNewBrandAndModel(Integer id) {
        RequestAddingNewModel requestAddingNewModel = requestAddingNewModelDAO
                .findById(id)
                .orElseThrow(() -> new UserException("request not found"));
        String newCarModel = requestAddingNewModel.getModel();
        String newCarBrand = requestAddingNewModel.getProducer();

        CarBrand brand = carBrandDAO.findByName(newCarBrand)
                .orElseGet(() -> carBrandDAO.save(new CarBrand(newCarBrand)));

        if (carModelDAO.existsByNameAndBrand(newCarModel, brand)) {
            throw new UserException("Model already exists");
        }
        CarModel model = new CarModel(newCarModel, brand);

        carBrandDAO.save(brand);
        carModelDAO.save(model);
        requestAddingNewModelDAO.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> removeNewBrandAndModel(Integer id) {
        RequestAddingNewModel request = requestAddingNewModelDAO
                .findById(id)
                .orElseThrow(() -> new UserException("Request not found"));
        requestAddingNewModelDAO.delete(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<RequestAddingNewModel>> getAllRequestNewBrandsAndModels() {
        List<RequestAddingNewModel> allRequestNewBrandsAndModels = requestAddingNewModelDAO.findAll();
        return new ResponseEntity<>(allRequestNewBrandsAndModels, HttpStatus.OK);
    }

    public ResponseEntity<List<String>> getAllAvailableRegions() {
        List<String> regions = Arrays.stream(Region.values())
                .map(Enum::name)
                .toList();
        return new ResponseEntity<>(regions, HttpStatus.OK);
    }

    private CarBrand getBrand(Integer brandId) {
        return carBrandDAO.findById(brandId)
                .orElseThrow(() -> new UserException("Brand not found"));
    }

    private CarModel getModel(Integer modelId, CarBrand brand) {
        CarModel model = carModelDAO.findById(modelId)
                .orElseThrow(() -> new UserException("Model not found"));
        if (!model.getBrand().getId().equals(brand.getId())) {
            throw new UserException("Model does not belong to selected brand");
        }
        return model;
    }

    private void validateCarData(CarInReview carInReview) {
        Currency[] availableCurrencies = new Currency[] {Currency.USD, Currency.EUR, Currency.UAH};
        if (!List.of(availableCurrencies).contains(carInReview.getOriginalCurrency())) {
            throw new UserException("Supported currencies are " + Currency.UAH + ", " + Currency.USD +  " and " + Currency.EUR);
        }

        CarBrand carBrand = carBrandDAO.findById(carInReview.getBrandId())
                .orElseThrow(() -> new UserException("Brand not found"));

        CarModel carModel = carModelDAO.findById(carInReview.getModelId())
                .orElseThrow(() -> new UserException("Model not found"));

        if (!carModel.getBrand().getId().equals(carBrand.getId())) {
            throw new UserException("Model does not belong to selected brand");
        }
    }

    public ResponseEntity<List<CarDTO>> getCarsForRegion(AppUser appUser, String value) {
        if (!EnumUtils.isValidEnum(Region.class, value)) {
            throw new UserException("Region " + value + " not found on the platform");
        }
        Region region = Region.valueOf(value);
        List<Car> cars = carDAO.findByRegion(region);
        List<CarDTO> carsDTO = new ArrayList<>();

        for (Car car: cars) {
            CarDTO carDTO = getCarInfo(car, appUser);
            carsDTO.add(carDTO);
        }
        return new ResponseEntity<>(carsDTO, HttpStatus.OK);
    }
}

/*
README;
Docker;
после этого AWS.


готово:
проверить/добавить approve для ручной модерации менеджером;
средняя цена brand + model по всей Украине;
просмотры за день / неделю / месяц;
закончить flow запроса отсутствующей марки/модели и их добавления менеджером/админом;
решить вопрос с permissions;
Postman collection с позитивными и негативными сценариями;


Я бы проверял в таком порядке:

Регистрация и логин.
USER может смотреть автомобили.
USER может начать создание объявления.
После успешной публикации становится SELLER.
BASIC не может иметь больше одного ACTIVE объявления.
PREMIUM может иметь несколько.
SELLER может редактировать только свой draft и продавать только свою машину.
Обычный пользователь не может удалить чужую машину, approve moderation, добавлять brand/model или блокировать пользователей.
Manager может модерировать, удалять объявления, работать с запросами brand/model, блокировать пользователей.
Admin дополнительно может создавать manager/admin.
Проверка profanity: 1-я, 2-я, 3-я попытка → после третьей WAITING_FOR_MANAGER.
Manager approve → автомобиль становится ACTIVE.
Проверить USD/EUR/UAH, originalPrice, originalCurrency, курсы PrivatBank и три рассчитанные цены.
Проверить avgPriceRegionUAH и avgPriceUkraineUAH.
Открыть одну машину несколько раз и проверить all/day/week/month views.
Проверить, что BASIC не получает Premium-статистику, а PREMIUM получает.

И по ходу этих проверок уже собирай настоящую Postman collection, а не тестируй одноразовыми запросами. Тогда требование про Postman практически выполнится одновременно с функциональным тестированием.
 */
