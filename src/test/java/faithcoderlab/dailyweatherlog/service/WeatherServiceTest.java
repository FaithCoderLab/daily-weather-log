package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.WeatherData;
import faithcoderlab.dailyweatherlog.repository.WeatherDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherApiClient weatherApiClient;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @DisplayName("API에서 날씨 데이터 가져오기 테스트")
    void getWeatherFromApiTest() {
        // given
        Map<String, Object> weatherResponse = new HashMap<>();
        Map<String, Object> mainData = new HashMap<>();
        mainData.put("temp", 20.5);
        weatherResponse.put("main", mainData);

        List<Map<String, Object>> weatherList = new ArrayList<>();
        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("main", "Sunny");
        weatherList.add(weatherDetails);
        weatherResponse.put("weather", weatherList);

        when(weatherApiClient.getWeatherData()).thenReturn(weatherResponse);

        // when
        WeatherService.WeatherDto result = weatherService.getWeatherFromApi();

        // then
        assertEquals("Sunny", result.description());
        assertEquals(20.5, result.temperature(), 0.01);
    }

    @Test
    @DisplayName("DB에서 날씨 데이터 가져오기 테스트")
    void getWeatherDataFromDatabaseTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        WeatherData weatherData = WeatherData.builder()
                .id(1L)
                .date(date)
                .weather("Cloudy")
                .temperature(15.5)
                .build();

        when(weatherDataRepository.findByDate(date)).thenReturn(Optional.of(weatherData));

        // when
        WeatherService.WeatherDto result = weatherService.getWeatherData(date);

        // then
        assertEquals("Cloudy", result.description());
        assertEquals(15.5, result.temperature(), 0.01);
        verify(weatherApiClient, never()).getWeatherData();
    }

    @Test
    @DisplayName("날씨 데이터 DB에 없을 때 API에서 가져오기 테스트")
    void getWeatherDataFromApiWhenNotInDatabaseTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);

        when(weatherDataRepository.findByDate(date)).thenReturn(Optional.empty());

        Map<String, Object> weatherResponse = new HashMap<>();
        Map<String, Object> mainData = new HashMap<>();
        mainData.put("temp", 22.0);
        weatherResponse.put("main", mainData);

        List<Map<String, Object>> weatherList = new ArrayList<>();
        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("main", "Sunny");
        weatherList.add(weatherDetails);
        weatherResponse.put("weather", weatherList);

        when(weatherApiClient.getWeatherData()).thenReturn(weatherResponse);

        // when
        WeatherService.WeatherDto result = weatherService.getWeatherData(date);

        // then
        assertEquals("Sunny", result.description());
        assertEquals(22.0, result.temperature(), 0.01);
        verify(weatherApiClient).getWeatherData();
    }

    @Test
    @DisplayName("새 날씨 데이터 저장 테스트")
    void saveNewWeatherDataTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);

        when(weatherDataRepository.findByDate(date)).thenReturn(Optional.empty());

        Map<String, Object> weatherResponse = new HashMap<>();
        Map<String, Object> mainData = new HashMap<>();
        mainData.put("temp", 22.0);
        weatherResponse.put("main", mainData);

        List<Map<String, Object>> weatherList = new ArrayList<>();
        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("main", "Sunny");
        weatherList.add(weatherDetails);
        weatherResponse.put("weather", weatherList);

        when(weatherApiClient.getWeatherData()).thenReturn(weatherResponse);

        // when
        weatherService.saveWeatherData(date);

        // then
        ArgumentCaptor<WeatherData> weatherDataCaptor = ArgumentCaptor.forClass(WeatherData.class);
        verify(weatherDataRepository).save(weatherDataCaptor.capture());

        WeatherData savedData = weatherDataCaptor.getValue();
        assertEquals(date, savedData.getDate());
        assertEquals("Sunny", savedData.getWeather());
        assertEquals(22.0, savedData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("기존 날씨 데이터 업데이트 테스트")
    void updateExistingWeatherDataTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);

        WeatherData existingData = WeatherData.builder()
                .id(1L)
                .date(date)
                .weather("Cloudy")
                .temperature(15.5)
                .build();

        when(weatherDataRepository.findByDate(date)).thenReturn(Optional.of(existingData));

        Map<String, Object> weatherResponse = new HashMap<>();
        Map<String, Object> mainData = new HashMap<>();
        mainData.put("temp", 22.0);
        weatherResponse.put("main", mainData);

        List<Map<String, Object>> weatherList = new ArrayList<>();
        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("main", "Sunny");
        weatherList.add(weatherDetails);
        weatherResponse.put("weather", weatherList);

        when(weatherApiClient.getWeatherData()).thenReturn(weatherResponse);

        // when
        weatherService.saveWeatherData(date);

        // then
        ArgumentCaptor<WeatherData> weatherDataCaptor = ArgumentCaptor.forClass(WeatherData.class);
        verify(weatherDataRepository).save(weatherDataCaptor.capture());

        WeatherData updatedData = weatherDataCaptor.getValue();
        assertEquals(1L, updatedData.getId());
        assertEquals(date, updatedData.getDate());
        assertEquals("Sunny", updatedData.getWeather());
        assertEquals(22.0, updatedData.getTemperature(), 0.01);
    }

    @Test
    @DisplayName("API 호출 실패 테스트")
    void apiCallFailureTest() {
        // given
        when(weatherApiClient.getWeatherData()).thenThrow(new RuntimeException("API 호출 실패"));

        // when & then
        Exception exception = assertThrows(RuntimeException.class,
                () -> weatherService.getWeatherFromApi());

        assertTrue(exception.getMessage().contains("Failed to get weather data"));
    }
}