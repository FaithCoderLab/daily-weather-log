package faithcoderlab.dailyweatherlog.service;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.repository.DiaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    @DisplayName("일기 생성 테스트")
    void createDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String text = "테스트 일기입니다";
        WeatherService.WeatherDto weatherData = new WeatherService.WeatherDto("Clear", 22.0);

        when(weatherService.getWeatherData(date)).thenReturn(weatherData);

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

    @Test
    @DisplayName("일기 조회 테스트")
    void readDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        List<Diary> expectedDiaries = new ArrayList<>();
        expectedDiaries.add(Diary.builder()
                .id(1L)
                .date(date)
                .text("테스트 일기")
                .weather("Clear")
                .temperature(22.0)
                .build());

        when(diaryRepository.findAllByDate(date)).thenReturn(expectedDiaries);

        // when
        List<Diary> actualDiaries = diaryService.readDiary(date);

        // then
        assertEquals(expectedDiaries.size(), actualDiaries.size());
        assertEquals(expectedDiaries.get(0).getId(), actualDiaries.get(0).getId());
        assertEquals(expectedDiaries.get(0).getText(), actualDiaries.get(0).getText());
        verify(diaryRepository).findAllByDate(date);
    }

    @Test
    @DisplayName("기간별 일기 조회 테스트")
    void readDiariesTest() {
        // given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Diary> expectedDiaries = new ArrayList<>();
        expectedDiaries.add(Diary.builder()
                .id(1L)
                .date(startDate)
                .text("시작일 일기")
                .weather("Cloudy")
                .temperature(15.0)
                .build());
        expectedDiaries.add(Diary.builder()
                .id(2L)
                .date(endDate)
                .text("종료일 일기")
                .weather("Sunny")
                .temperature(25.0)
                .build());

        when(diaryRepository.findAllByDateBetween(startDate, endDate)).thenReturn(expectedDiaries);

        // when
        List<Diary> actualDiaries = diaryService.readDiaries(startDate, endDate);

        // then
        assertEquals(expectedDiaries.size(), actualDiaries.size());
        assertEquals(expectedDiaries.get(0).getId(), actualDiaries.get(0).getId());
        assertEquals(expectedDiaries.get(1).getId(), actualDiaries.get(1).getId());
        verify(diaryRepository).findAllByDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("일기 수정 테스트")
    void updateDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String newText = "수정된 일기";

        Diary existingDiary = Diary.builder()
                .id(1L)
                .date(date)
                .text("원본 일기")
                .weather("Cloudy")
                .temperature(15.5)
                .build();

        List<Diary> diaries = new ArrayList<>();
        diaries.add(existingDiary);

        when(diaryRepository.findAllByDate(date)).thenReturn(diaries);

        // when
        diaryService.updateDiary(date, newText);

        // then
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        verify(diaryRepository).save(diaryCaptor.capture());

        Diary updatedDiary = diaryCaptor.getValue();
        assertEquals(date, updatedDiary.getDate());
        assertEquals(newText, updatedDiary.getText());
        assertEquals("Cloudy", updatedDiary.getWeather());
        assertEquals(15.5, updatedDiary.getTemperature(), 0.1);
    }

    @Test
    @DisplayName("존재하지 않는 일기 수정 시 예외 발생 테스트")
    void updateDiaryWhenNoDiaryExistsTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String newText = "수정할 일기";

        when(diaryRepository.findAllByDate(date)).thenReturn(new ArrayList<>());

        // when & then
        Exception exception = assertThrows(RuntimeException.class,
                () -> diaryService.updateDiary(date, newText));

        assertTrue(exception.getMessage().contains("해당 날짜의 일기가 존재하지 않습니다"));
        verify(diaryRepository, never()).save(any(Diary.class));
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        doNothing().when(diaryRepository).deleteAllByDate(date);

        // when
        diaryService.deleteDiary(date);

        // then
        verify(diaryRepository).deleteAllByDate(date);
    }

    @Test
    @DisplayName("날씨 일기 자동 생성 테스트")
    void createWeatherDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        WeatherService.WeatherDto weatherData = new WeatherService.WeatherDto("Sunny", 25.0);

        when(weatherService.getWeatherData(date)).thenReturn(weatherData);

        // when
        diaryService.createWeatherDiary(date);

        // then
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        verify(diaryRepository).save(diaryCaptor.capture());

        Diary savedDiary = diaryCaptor.getValue();
        assertEquals(date, savedDiary.getDate());
        assertTrue(savedDiary.getText().contains("자동 수집된 날씨 정보"));
        assertTrue(savedDiary.getText().contains("Sunny"));
        assertEquals("Sunny", savedDiary.getWeather());
        assertEquals(25.0, savedDiary.getTemperature(), 0.1);
    }
}