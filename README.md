# Weather Diary API Project

A Spring Boot based backend project that allows you to create, view, update, and delete diary entries with weather information.

## Project Overview

This project provides APIs for users to write and manage daily diary entries by date. It utilizes the OpenWeatherMap API to save current weather information along with diary entries and uses a scheduler to automatically collect weather data daily.

## Technology Stack

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- MySQL
- OpenWeatherMap API
- Swagger (SpringDoc)
- JUnit 5
- Logback

## Key Features

### API List

- **Create Diary** - `POST /create/diary`
  - Receives date and diary text, saves it with weather information

- **Read Diary (by date)** - `GET /read/diary`
  - Retrieves all diary entries for a specific date

- **Read Diaries (by period)** - `GET /read/diaries`
  - Retrieves all diary entries between start and end dates
  
- **Update Diary** - `PUT /update/diary`
  - Updates the first diary entry for a specific date

- **Delete Diary** - `DELETE /delete/diary`
  - Deletes all diary entries for a specific date

### Additional Features

- **Transaction Management**
  - All database-related functions are transactional
  
- **Automated Weather Data Collection**
  - Collects and stores weather data daily at 1 AM via OpenWeatherMap API
  
- **Logging**
  - Application logging via Logback
  
- **Exception Handling**
  - Centralized exception handling with ExceptionHandler
  
- **API Documentation**
  - Automatic API documentation generation using Swagger
  
- **Test Code**
  - Feature validation through unit and integration tests

## Project Structure

```
src
├── main
│   ├── java
│   │   └── faithcoderlab
│   │       └── dailyweatherlog
│   │           ├── config
│   │           │   ├── AppConfig.java
│   │           │   ├── SchedulerConfig.java
│   │           │   └── SwaggerConfig.java
│   │           ├── controller
│   │           │   └── DiaryController.java
│   │           ├── exception
│   │           │   └── GlobalExceptionHandler.java
│   │           ├── model
│   │           │   ├── Diary.java
│   │           │   └── WeatherData.java
│   │           ├── repository
│   │           │   ├── DiaryRepository.java
│   │           │   └── WeatherDataRepository.java
│   │           └── service
│   │               ├── DiaryService.java
│   │               ├── WeatherApiClient.java
│   │               ├── WeatherSchedulerService.java
│   │               └── WeatherService.java
│   └── resources
│       └── application.properties
└── test
    └── java
        └── faithcoderlab
            └── dailyweatherlog
                ├── controller
                │   └── DiaryControllerTest.java
                ├── integration
                │   └── DiaryIntegrationTest.java
                └── service
                    ├── DiaryServiceTest.java
                    ├── WeatherApiClientTest.java
                    ├── WeatherSchedulerServiceTest.java
                    └── WeatherServiceTest.java
```

## Installation and Execution

1. **Clone the project**
   ```
   git clone https://github.com/FaithCoderLab/daily-weather-log.git
   cd daily-weather-log
   ```

2. **Configure application.properties**
   ```
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   ```
   - Enter database settings and OpenWeatherMap API key in the copied file

3. **Create database**
   ```sql
   CREATE DATABASE weatherlog;
   ```

4. **Build and run the project**
   ```
   ./gradlew build
   ./gradlew bootRun
   ```

5. **Access API documentation**
   - http://localhost:8080/swagger-ui.html

## Testing

Run unit and integration tests:
```
./gradlew test
```

## License

This project is distributed under the MIT License.
