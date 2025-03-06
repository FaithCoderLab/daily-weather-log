package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.repository.DiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private WeatherApiClient weatherApiClient;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    void createDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String text = "Test diary entry";

        Map<String, Object> weatherData = new HashMap<>();

        Map<String, Object> mainData = new HashMap<>();
        mainData.put("temp", 22.0);
        weatherData.put("main", mainData);

        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("main", "Clear");
        weatherData.put("weather", List.of(weatherDetails));

        when(weatherApiClient.getWeatherData()).thenReturn(weatherData);

        // when
        diaryService.createDiary(date, text);

        // then
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        verify(diaryRepository).save(diaryCaptor.capture());

        Diary savedDiary = diaryCaptor.getValue();
        assertEquals(date, savedDiary.getDate());
        assertEquals(text, savedDiary.getText());
        assertEquals("Clear", savedDiary.getWeather());
        assertEquals(22.0, savedDiary.getTemperature(), 0.1);
    }
}