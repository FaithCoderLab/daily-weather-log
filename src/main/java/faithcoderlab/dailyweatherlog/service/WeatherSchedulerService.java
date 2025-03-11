package faithcoderlab.dailyweatherlog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherSchedulerService {

    private final WeatherService weatherService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void saveWeatherDataDaily() {
        log.info("Daily weather data collection scheduled task starting...");

        LocalDate today = LocalDate.now();

        try {
            weatherService.saveWeatherData(today);
            log.info("Daily weather data successfully collected and saved");
        } catch (Exception e) {
            log.error("Error while collecting daily weather data: {}", e.getMessage(), e);
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
    @Transactional
    public void saveWeatherDataOnStartUp() {
        log.info("Collecting weather data on application startup...");

        LocalDate today = LocalDate.now();

        try {
            weatherService.saveWeatherData(today);
            log.info("Application startup weather data successfully collected and saved");
        } catch (Exception e) {
            log.error("Error while collecting startup weather data: {}", e.getMessage(), e);
        }
    }
}
