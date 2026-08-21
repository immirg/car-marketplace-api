# Car Marketplace API
Car Marketplace is a backend REST API for a car sales platform.

The project is inspired by services such as AutoRia.  
The main goal is to create a flexible backend where users can browse cars, create advertisements, manage their own cars, and use additional Premium features.
The platform also includes moderation, user roles, permissions, car statistics, currency conversion, average market price calculation, and management of car brands and models.
The backend is built with Spring Boot and uses MySQL as the main database.

# Main Idea
The platform has several types of users:

- Guests
- Registered users
- Sellers
- Platform managers
- Platform administrators

A guest can browse public car information without registration.
A registered user can create a car advertisement.
After a user successfully publishes a car, the user becomes a seller.
Managers and administrators have additional permissions for moderation and platform management.

# User Roles
The application currently has the following roles.

# USER
This is the default role after registration.

A user can:
- Browse cars
- Create a car advertisement
- Edit their draft advertisement
- Delete their draft
- Upgrade the account to Premium

After the first car is successfully published, the user becomes a seller.

# SELLER
A seller can:
- Browse cars
- Create advertisements
- Edit their own advertisements
- View their active cars
- View their sold cars
- Mark their own car as sold
- Request a new car brand or model
- Upgrade to Premium

A seller can manage only their own advertisements.

# PLATFORM_MANAGER
A platform manager can:

- View advertisements waiting for manual moderation
- Approve advertisements
- Remove invalid advertisements
- Delete published advertisements
- Block users
- Unblock users
- View requests for new car brands and models
- Approve or reject new brand/model requests

# PLATFORM_ADMIN
A platform administrator has all platform permissions.
An administrator can also:
- Create platform managers
- Create other administrators
- Perform all manager operations
- Manage all protected platform functionality

# Permission System
The project uses a permission-based security system.
Roles contain a set of permissions.
Examples of permissions:
- `CAR_READ`
- `CAR_CREATE`
- `CAR_EDIT_OWN`
- `CAR_SELL_OWN`
- `REVIEW_MODERATE`
- `CAR_DELETE_ANY`
- `BRAND_MODEL_MANAGE`
- `USER_BLOCK`
- `MANAGER_CREATE`
- `PREMIUM_BUY`
- `PREMIUM_STATISTICS`

Spring Security checks permissions before protected API endpoints are executed.

For example:
SELLER
    ↓
CAR_SELL_OWN
    ↓
PATCH /car/{id}/sell

The service layer also performs additional checks.
For example, `CAR_SELL_OWN` gives a seller permission to sell their own car, but the service also checks that the selected car really belongs to this seller.
This architecture makes the system more flexible because new roles can be added later without changing all existing endpoints.

# Authentication
The application uses JWT authentication.
After login, the client receives an access token.

The project also supports refresh tokens.
Public endpoints do not require authentication.

# Guest Access
A guest does not need an account to browse the car catalog.
Public functionality includes:
- View active cars
- View car information
- Search cars by brand
- Search cars by power
- View available regions
- View available car brands
- View models for a selected brand

Actions that modify data require authentication.
For example:
- Create advertisement
- Edit advertisement
- Sell car
- Delete draft
- Upgrade account
- Moderation
- User management

# Account Types
The platform has two account types:
- `BASIC`
- `PREMIUM`

# BASIC
A Basic account is the default account type.
A Basic seller can have only one active car advertisement.
If the seller already has one active advertisement, the platform does not allow another active advertisement to be published.

# PREMIUM
A Premium seller can have multiple active advertisements.
Premium also provides additional advertisement statistics.
The current project does not include a real payment provider.  
The Premium purchase process is mocked through an API endpoint.

# Premium Statistics
Premium users can see additional information for their own advertisements.
The statistics include:
- Total number of views
- Views for the current day
- Views for the current week
- Views for the current month
- Average price of the same car in the selected region
- Average price of the same car in Ukraine

Basic users do not receive this Premium information.

# Car View Statistics
Every time a car advertisement is opened, a new view record is created.
A view contains:
- Car
- Date and time of the view

Example:
car_view
id | car_id | viewed_at
1  | 15     | 2026-08-20 12:10
2  | 15     | 2026-08-20 12:15
3  | 20     | 2026-08-20 12:20

The application uses these records to calculate:
- All views
- Daily views
- Weekly views
- Monthly views

The statistics are calculated from stored view timestamps.

# Car Advertisement Creation
A registered user can create a new car advertisement.
The client sends information such as:
```json
{
  "brandId": 1,
  "modelId": 1,
  "power": 250,
  "imageUrl": "https://example.com/bmw.jpg",
  "originalPrice": 3000,
  "originalCurrency": "USD",
  "phoneNumber": "+380991234567",
  "description": "Car description",
  "region": "KYIV"
}
```
The server validates the provided data.
The application checks:
- Brand exists
- Model exists
- Model belongs to the selected brand
- Currency is supported
- Region is valid
- Power is within the allowed range
- The user does not already have another unfinished draft
- The Basic account limit is not exceeded

The advertisement is first stored as an advertisement under review.

# Advertisement Draft
If an advertisement requires editing, it is stored in the `for_verification` table.
A user can have one advertisement in the editing process.
The client can request the current draft:
GET /cars/reviews/current

The user can continue editing the existing draft instead of creating many unfinished advertisements.
The draft can also be deleted.

# Automatic Advertisement Moderation
Every new advertisement is automatically checked for forbidden words.
If no forbidden words are found, the advertisement can continue to publication.
If forbidden words are found:

1. The advertisement stays in the editing process.
2. The user receives information that the description must be changed.
3. The edit attempt counter is increased.
4. The user can edit the advertisement again.

The number of editing attempts is limited.
After the allowed number of failed attempts, the advertisement is moved to manual moderation.
The advertisement receives the status:

WAITING_FOR_MANAGER
A platform manager can then review it.

# Manual Moderation
Platform managers can request advertisements waiting for review.
A manager can:
- Approve an advertisement
- Remove an advertisement from review
- Delete an invalid advertisement

Before approval, the system checks that the advertisement is actually in:
WAITING_FOR_MANAGER

The Basic account active advertisement limit is also checked again before publication.
If the advertisement is approved, the final `Car` entity is created and the advertisement becomes active.

# Advertisement Status
Cars and advertisements use statuses to represent their current state.
Examples:
NEEDS_EDIT
WAITING_FOR_MANAGER
ACTIVE
SOLD

Typical flow:
Create advertisement
        ↓
Automatic moderation
        ↓
     ┌───────────────┐
     │               │
No forbidden     Forbidden
words            words
     │               │
     ↓               ↓
   ACTIVE        NEEDS_EDIT
                     ↓
                  Editing
                     ↓
               Failed attempts
                     ↓
             WAITING_FOR_MANAGER
                     ↓
              Manager approval
                     ↓
                   ACTIVE
                     ↓
                Seller sells car
                     ↓
                    SOLD


# Car Brands and Models
Car brands and models are stored in the database.
Example:
BMW
 ├── X5
 ├── X6
 └── X7

Daewoo
 └── Lanos

The client first requests all available brands.
Example response:
```json
[
  {
    "id": 1,
    "name": "BMW"
  },
  {
    "id": 2,
    "name": "Daewoo"
  }
]
```
After the user selects a brand, the client requests models for this brand.
Example:
GET /car-brands/1/models

Example response:
```json
[
  {
    "brand": "BMW",
    "model": "X5",
    "id": 1
  }
]
```
The IDs are used when a new car advertisement is created.

# Requesting a New Brand or Model
If the required car brand or model is not available, a seller can create a request.

Example:
```json
{
  "producer": "BMW",
  "model": "X7"
}
```
The request is stored in the database.
Platform managers can:
- View all new brand/model requests
- Approve a request
- Reject a request

If a manager approves the request, the new brand or model is added to the database.
The system also checks whether the brand already exists before creating a new brand.

# Regions
A car advertisement contains the region where the car is sold.
Regions are represented by an enum.
Examples:
KYIV
LVIV
ODESA
DNIPRO
KHARKIV

The client can request the list of available regions.
The selected region is saved with the car advertisement.
The region is also used for average price calculation.

# Currency Support
The platform supports three currencies:
- `UAH`
- `USD`
- `EUR`

The seller enters a price in only one currency.
Example:
```json
{
  "originalPrice": 3500,
  "originalCurrency": "USD"
}
```
The original price and original currency are always saved.
The application then calculates the price in all supported currencies.
Example:
```json
{
  "originalPrice": 3500,
  "originalCurrency": "USD",
  "priceUAH": 155050,
  "priceUSD": 3500,
  "priceEUR": 2958.97
}
```

# Exchange Rates
Exchange rates are received from PrivatBank.
The application stores:
- USD buy rate
- USD sale rate
- EUR buy rate
- EUR sale rate

Example:
```json
{
  "usdBuy": 44.3,
  "usdSale": 44.9,
  "eurBuy": 51.4,
  "eurSale": 52.4
}
```
The rate used for calculation is stored with the car.
This allows the platform to know which exchange rate was used when the price was calculated.
The application stores exchange rates in the `exchange_rate` table.
When a new car is finally published, the application takes the current exchange rate from the database and calculates all car prices.
This calculation is performed only when the advertisement is ready for publication. It is not necessary to recalculate currency prices every time the user edits a draft.

# Automatic Exchange Rate Update
The project contains scheduled logic for exchange rate updates.
The expected update flow is:
Get new exchange rates
        ↓
Save exchange rates
        ↓
Recalculate prices of active cars
        ↓
Recalculate average prices

The exchange rate update is designed to run automatically once per day.
Active car prices are recalculated using the latest stored exchange rates.
After the prices are updated, average market prices are recalculated.

> The scheduled execution should be verified before the final project submission.

# Average Car Price
The application calculates two average prices:
- Average price in the selected region
- Average price in Ukraine

The calculation uses active advertisements with the same:
brand + model

For a regional average, the region is also taken into account.
Example:
BMW X5 / KYIV

Car 1 = 1,000,000 UAH
Car 2 = 1,200,000 UAH
Car 3 = 1,400,000 UAH

Average:
1,200,000 UAH

The regional average is stored in:
avgPriceRegionUAH

The average for all Ukraine is stored in:
avgPriceUkraineUAH

Average prices are updated when:
- A new car is published
- A car is sold
- A car is deleted
- Active car prices are recalculated

This keeps the statistics up to date.

# Selling a Car
A seller can mark their own car as sold.
Before changing the status, the application checks that the current user is the owner of the selected car.
The status changes from:
ACTIVE
to:
SOLD

After the status change, average prices are recalculated because the sold car should not be included in active market statistics.

# User Blocking
Platform managers and administrators can block users.
A blocked user cannot use protected platform functionality.
The application checks account status during authentication.
Managers can also unblock users.

# Initial Administrator
The application creates the initial platform administrator during application startup.
The administrator is created only if the required administrator account does not already exist.
This logic allows a new environment to start without manually creating the first administrator directly in MySQL.
The administrator is created by the application initializer.
For security reasons, administrator credentials should be configured outside the source code for a real production environment.

# Application Initialization
The project uses initialization classes for required platform data.
Initialization can include:

- Initial administrator
- Initial car brands
- Initial car models
- Required reference data

Application initialization runs when Spring Boot starts.
Demo cars are not automatically created. Cars should be created through the real API flow.

# Car Catalog
- Get all cars
- Get car by ID
- Get cars by brand
- Get cars by power
- Get available regions
- Get available brands
- Get models by brand

# My Ads
- Create car advertisement
- Get current draft
- Edit current draft
- Delete current draft
- Get active cars
- Get sold cars
- Mark car as sold

# Moderation
- Get advertisements waiting for moderation
- Approve advertisement
- Remove advertisement from review
- Delete published advertisement

# Brand and Model Requests
- Request new brand/model
- Get requests
- Approve request
- Reject request

# User Management
- Block user
- Unblock user
- Make manager
- Make administrator

# Premium
- Upgrade account to Premium

# Docker
The application can be started with Docker Compose.
Docker Compose starts two containers:
- Spring Boot application
- MySQL database
The application container connects to MySQL using the Docker service name.