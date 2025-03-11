package faithcoderlab.dailyweatherlog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiClient weatherApiClient;

    public WeatherData getWeatherData() {
        log.info("Fetching current weather data");

        try {
            Map<String, Object> weatherData = weatherApiClient.getWeatherData();

            Map<String, Object> mainData = (Map<String, Object>) weatherData.get("main");
            Double temperature = ((Number) mainData.get("temp")).doubleValue();

            Map<String, Object> weatherDetails = (Map<String, Object>) ((java.util.List<?>) weatherData.get("weather")).get(0);
            String weatherDescription = (String) weatherDetails.get("main");

            return new WeatherData(weatherDescription, temperature);
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get weather data", e);
        }
    }

    public record WeatherData(String description, double temperature) {}
}
