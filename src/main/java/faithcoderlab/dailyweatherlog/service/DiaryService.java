package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final WeatherApiClient weatherApiClient;

    @Transactional
    public void createDiary(LocalDate date, String text) {
        log.info("Creating diary entry for date: {}", date);

        Map<String, Object> weatherData = weatherApiClient.getWeatherData();

        Map<String, Object> mainData = (Map<String, Object>) weatherData.get("main");
        Double temperature = ((Number) mainData.get("temp")).doubleValue();

        Map<String, Object> weatherDetails = (Map<String, Object>) ((java.util.List<?>) weatherData.get("weather")).get(0);
        String weatherDescription = (String) weatherDetails.get("main");

        Diary diary = Diary.builder()
                .date(date)
                .text(text)
                .weather(weatherDescription)
                .temperature(temperature)
                .build();

        diaryRepository.save(diary);
        log.info("Diary entry created successfully for date: {}", date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        log.info("Reading diary entries for date: {}", date);
        return diaryRepository.findAllByDate(date);
    }
}
