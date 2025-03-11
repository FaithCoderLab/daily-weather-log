package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.WeatherData;
import faithcoderlab.dailyweatherlog.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiClient weatherApiClient;
    private final WeatherDataRepository weatherDataRepository;

    public WeatherDto getWeatherFromApi() {
        log.info("Fetching current weather data from API");

        try {
            Map<String, Object> weatherData = weatherApiClient.getWeatherData();

            Map<String, Object> mainData = (Map<String, Object>) weatherData.get("main");
            Double temperature = ((Number) mainData.get("temp")).doubleValue();

            Map<String, Object> weatherDetails = (Map<String, Object>) ((java.util.List<?>) weatherData.get("weather")).get(0);
            String weatherDescription = (String) weatherDetails.get("main");

            return new WeatherDto(weatherDescription, temperature);
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get weather data", e);
        }
    }

    @Transactional(readOnly = true)
    public WeatherDto getWeatherData(LocalDate date) {
        log.info("Getting weather data for date: {}", date);

        Optional<WeatherData> weatherDataOptional = weatherDataRepository.findByDate(date);

        if (weatherDataOptional.isPresent()) {
            log.info("Found weather data in database for date: {}", date);
            WeatherData data = weatherDataOptional.get();
            return new WeatherDto(data.getWeather(), data.getTemperature());
        } else {
            log.info("No weather data found in database for date: {}, fetching from API", date);
            return getWeatherFromApi();
        }
    }

    @Transactional
    public void saveWeatherData(LocalDate date) {
        log.info("Saving weather data for date: {}", date);

        WeatherDto weatherDto = getWeatherFromApi();

        Optional<WeatherData> existingWeatherData = weatherDataRepository.findByDate(date);

        if (existingWeatherData.isPresent()) {
            log.info("Updating existing weather data for date: {}", date);
            WeatherData weatherData = existingWeatherData.get();
            weatherData.setWeather(weatherDto.description());
            weatherData.setTemperature(weatherDto.temperature());
            weatherDataRepository.save(weatherData);
        } else {
            log.info("Creating new weather data for date: {}", date);
            WeatherData weatherData = WeatherData.builder()
                    .date(date)
                    .weather(weatherDto.description())
                    .temperature(weatherDto.temperature())
                    .build();
            weatherDataRepository.save(weatherData);
        }

        log.info("Weather data saved successfully for date: {}", date);
    }

    public record WeatherDto(String description, double temperature) {}
}
