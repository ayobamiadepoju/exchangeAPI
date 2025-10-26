# Country Currency and Exchange API

A Spring Boot project that integrates data from two external APIs to provide detailed country information, including population, GDP (estimated), and exchange rates. It supports fetching, filtering, sorting, deleting, and monitoring the refresh status of country data.

---

## Features

- Fetch country and exchange rate data from external APIs.
- Persist all country data into a relational database.
- Filter and sort countries by name, region, currency, GDP, and population.
- Retrieve a single country by name.
- Delete country records.
- View total countries and last refresh timestamp.
- Generate a summary image showing top 5 countries by GDP.

---

## External APIs Used

| Data Type | API URL |
|------------|----------|
| Countries  | `https://restcountries.com/v2/all?fields=name,capital,region,population,flag,currencies` |
| Exchange Rates | `https://open.er-api.com/v6/latest/USD` |

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA / Hibernate**
- **RestTemplate** for API calls
- **MySQL** (configurable)
- **Lombok** for boilerplate reduction
- **Maven** for dependency management

---

## Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/<your-username>/countryCurrencyAndExchangeAPI.git
cd countryCurrencyAndExchangeAPI
```

### 2️⃣ Configure the Database
Edit `application.properties` or `application.yml`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/country_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

Countries=https://restcountries.com/v2/all?fields=name,capital,region,population,flag,currencies
Exchange.rates=https://open.er-api.com/v6/latest/USD
```

### 3️⃣ Build and Run
```bash
mvn spring-boot:run
```

The server will start on: `http://localhost:8080`

---

## API Endpoints

### Refresh Country Data
**POST** `/countries/refresh`

Fetches data from both APIs, merges them, saves or updates entries, and stores timestamp.

**Response:** `200 OK` if successful.

---

### Get All Countries
**GET** `/countries`

Supports filtering and sorting.

**Parameters:**
| Query | Description | Example |
|--------|-------------|----------|
| `region` | Filter by region | `region=Africa` |
| `currency` | Filter by currency | `currency=NGN` |
| `name` | Filter by name | `name=Nigeria` |
| `sort` | Sort order | `sort=gdp_desc` or `population_asc` |

**Example:**
```
GET /countries?region=Europe&sort=gdp_desc
```

---

### Get Country by Name
**GET** `/countries/{name}`

Returns one country record.

**Example:**
```
GET /countries/Nigeria
```

---

### Delete a Country
**DELETE** `/countries/{name}`

Deletes a country record by name.

**Example:**
```
DELETE /countries/Nigeria
```

---

### Status Endpoint
**GET** `/countries/status`

Returns the total number of countries and the last refresh timestamp.

**Response:**
```json
{
  "totalCountries": 249,
  "lastRefreshedAt": "2025-10-26T09:00:00"
}
```

---

### Summary Image
**GET** `/countries/image`

Generates and serves a summary image showing:
- Total countries
- Last refresh timestamp
- Top 5 countries by GDP

**Response:** PNG image

---

## 🧠 Architecture Overview

```
├── controller
│   └── exchangeRatesController.java
├── service
│   └── CountryService.java
├── exception
│   └── GlobalExceptionHandler.java
├── model
│   └── Country.java
├── repository
│   └── CountryRepository.java
├── dto
│   ├── CountryUrlResponse.java
│   ├── ExchangeUrlResponse.java
│   └── StatusResponse.java
└── resources
    └── application.properties
```

---

## Example Flow
1. Run the app.
2. `POST /countries/refresh` → Fetch & save all countries.
3. `GET /countries` → View data in DB.
4. `GET /countries/status` → Check refresh status.
5. `GET /countries/image` → Generate summary PNG.

---

## Deployment Notes
- Make sure MySQL is configured on the server.
- Set environment variables for DB credentials.
- Use `spring.jpa.hibernate.ddl-auto=update` or `validate` in production.
- Serve image files from `/cache/` directory.

---
## 👤 Author

**Name:** AYOBAMI ADEPOJU  
**Email:** ayobamiadepoju263@gmail.com  
**Stack:** Java / Spring Boot  
**GitHub:** [@ayobamiadepoju](https://github.com/ayobamiadepoju)
---il:** adepojuopeyemi251@gmail.com

