package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final WeatherService weatherService;

    @Transactional
    public void createDiary(LocalDate date, String text) {
        log.info("Creating diary entry for date: {}", date);

        WeatherService.WeatherDto weatherData = weatherService.getWeatherData(date);

        Diary diary = Diary.builder()
                .date(date)
                .text(text)
                .weather(weatherData.description())
                .temperature(weatherData.temperature())
                .build();

        diaryRepository.save(diary);
        log.info("Diary entry created successfully for date: {}", date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        log.info("Reading diary entries for date: {}", date);
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        log.info("Reading diary entries from {} to {}", startDate, endDate);
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        log.info("Updating first diary entry for date: {}", date);
        List<Diary> diaryList = diaryRepository.findAllByDate(date);

        if (diaryList.isEmpty()) {
            log.error("No diary found for date: {}", date);
            throw new RuntimeException("해당 날짜의 일기가 존재하지 않습니다: " + date);
        }

        Diary firstDiary = diaryList.get(0);

        firstDiary.setText(text);

        diaryRepository.save(firstDiary);
        log.info("Diary entry updated successfully for date: {}", date);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        log.info("Deleting all diary entries for date: {}", date);
        diaryRepository.deleteAllByDate(date);
        log.info("Diary entry deleted successfully for date: {}", date);
    }

    @Transactional
    public void createWeatherDiary(LocalDate date) {
        log.info("Creating weather diary entry for date: {}", date);

        WeatherService.WeatherDto weatherData = weatherService.getWeatherData(date);

        Diary diary = Diary.builder()
                .date(date)
                .text("자동 수집된 날씨 정보: " + weatherData.description() + ", " + weatherData.temperature() + "°C")
                .weather(weatherData.description())
                .temperature(weatherData.temperature())
                .build();

        diaryRepository.save(diary);
        log.info("Weather diary entry created successfully for date: {}", date);
    }
}
