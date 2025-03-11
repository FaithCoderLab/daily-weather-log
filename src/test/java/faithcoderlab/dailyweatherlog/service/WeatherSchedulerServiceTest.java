package faithcoderlab.dailyweatherlog.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherSchedulerServiceTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherSchedulerService weatherSchedulerService;

    @Test
    @DisplayName("일일 날씨 데이터 스케줄러 테스트")
    void saveWeatherDataDailyTest() {
        // given
        doNothing().when(weatherService).saveWeatherData(any(LocalDate.class));

        // when
        weatherSchedulerService.saveWeatherDataDaily();

        // then
        verify(weatherService, times(1)).saveWeatherData(any(LocalDate.class));
    }

    @Test
    @DisplayName("애플리케이션 시작 시 날씨 데이터 수집 테스트")
    void saveWeatherDataOnStartUpTest() {
        // given
        doNothing().when(weatherService).saveWeatherData(any(LocalDate.class));

        // when
        weatherSchedulerService.saveWeatherDataOnStartUp();

        // then
        verify(weatherService, times(1)).saveWeatherData(any(LocalDate.class));
    }

    @Test
    @DisplayName("날씨 데이터 수집 중 예외 발생 시 처리 테스트")
    void saveWeatherDataWithExceptionTest() {
        // given
        doThrow(new RuntimeException("API 호출 오류")).when(weatherService).saveWeatherData(any(LocalDate.class));

        // when
        weatherSchedulerService.saveWeatherDataDaily();

        // then
        verify(weatherService, times(1)).saveWeatherData(any(LocalDate.class));
    }
}