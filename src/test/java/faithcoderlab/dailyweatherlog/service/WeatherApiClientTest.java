package faithcoderlab.dailyweatherlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherApiClientTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private WeatherApiClient weatherApiClient;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(weatherApiClient, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherApiClient, "apiUrl", "https://api.example.com/weather");
        ReflectionTestUtils.setField(weatherApiClient, "city", "Seoul");

        weatherApiClient = spy(weatherApiClient);
    }

    @Test
    @DisplayName("날씨 API 응답 파싱 테스트")
    void getWeatherDataTest() throws Exception {
        // given
        String jsonResponse = "{"
                + "\"weather\": [{\"id\": 800, \"main\": \"Clear\", \"description\": \"clear sky\", \"icon\": \"01d\"}],"
                + "\"main\": {\"temp\": 22.5, \"humidity\": 60, \"pressure\": 1010},"
                + "\"name\": \"Seoul\""
                + "}";

        doReturn(jsonResponse).when(weatherApiClient).getWeatherString();

        // when
        Map<String, Object> result = weatherApiClient.getWeatherData();

        // then
        assertNotNull(result);
        assertEquals("Seoul", result.get("name"));

        // main 객체 확인
        @SuppressWarnings("unchecked")
        Map<String, Object> main = (Map<String, Object>) result.get("main");
        assertNotNull(main);
        assertEquals(22.5, main.get("temp"));

        // weather 배열 확인
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> weather = (java.util.List<Map<String, Object>>) result.get("weather");
        assertNotNull(weather);
        assertEquals(1, weather.size());
        assertEquals("Clear", weather.get(0).get("main"));
    }

    @Test
    @DisplayName("API 호출 실패 시 예외 발생 테스트")
    void getWeatherDataFailureTest() throws Exception {
        // given
        doThrow(new RuntimeException("Failed to get response from weather API"))
                .when(weatherApiClient).getWeatherString();

        // when & then
        Exception exception = assertThrows(RuntimeException.class,
                () -> weatherApiClient.getWeatherData());

        assertTrue(exception.getMessage().contains("Failed to get response from weather API"));
    }

    @Test
    @DisplayName("응답 파싱 실패 테스트")
    void parseWeatherDataFailureTest() throws Exception {
        // given
        String invalidJsonResponse = "Invalid JSON";

        doReturn(invalidJsonResponse).when(weatherApiClient).getWeatherString();

        // when & then
        Exception exception = assertThrows(RuntimeException.class,
                () -> weatherApiClient.getWeatherData());

        assertTrue(exception.getMessage().contains("Failed to parse weather data"));
    }
}