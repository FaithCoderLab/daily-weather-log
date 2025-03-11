package faithcoderlab.dailyweatherlog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherApiClient {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.api.city}")
    private String city;

    private final ObjectMapper objectMapper;

    public Map<String, Object> getWeatherData() {
        log.info("Fetching weather data for city: {}", city);
        String weatherString = getWeatherString();
        return parseWeatherData(weatherString);
    }

    String getWeatherString() {
        String fullApiUrl = apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";
        log.debug("API request URL: {}", fullApiUrl);

        try {
            BufferedReader br = getBufferedReader(fullApiUrl);

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();
            String responseStr = response.toString();
            log.debug("Received response from API: {}", responseStr);
            return responseStr;
        } catch (IOException e) {
            log.error("Error getting weather data from API: {}", e.getMessage());
            throw new RuntimeException("Failed to get response from weather API", e);
        }
    }

    private Map<String, Object> parseWeatherData(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (Exception e) {
            log.error("Error parsing weather data: {}", e.getMessage());
            throw new RuntimeException("Failed to parse weather data", e);
        }
    }

    private static BufferedReader getBufferedReader(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();

        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            throw new IOException("OpenWeatherMap API request failed with response code: " + responseCode);
        }
        return br;
    }
}
