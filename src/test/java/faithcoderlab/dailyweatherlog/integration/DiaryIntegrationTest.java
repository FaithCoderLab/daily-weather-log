package faithcoderlab.dailyweatherlog.integration;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.repository.DiaryRepository;
import faithcoderlab.dailyweatherlog.service.WeatherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DiaryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiaryRepository diaryRepository;

    @MockBean
    private WeatherService weatherService;

    @AfterEach
    void cleanup() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("일기 생성 및 조회 통합 테스트")
    void createAndReadDiaryTest() throws Exception {
        // given
        LocalDate today = LocalDate.now();
        String text = "통합 테스트용 일기";

        when(weatherService.getWeatherData(any(LocalDate.class)))
                .thenReturn(new WeatherService.WeatherDto("Sunny", 25.0));

        mockMvc.perform(post("/create/diary")
                        .param("date", today.toString())
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        List<Diary> diaries = diaryRepository.findAllByDate(today);
        assertEquals(1, diaries.size());
        assertEquals(text, diaries.get(0).getText());
        assertEquals("Sunny", diaries.get(0).getWeather());
        assertEquals(25.0, diaries.get(0).getTemperature(), 0.1);

        mockMvc.perform(get("/read/diary")
                        .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value(text))
                .andExpect(jsonPath("$[0].weather").value("Sunny"))
                .andExpect(jsonPath("$[0].temperature").value(25.0));
    }

    @Test
    @DisplayName("일기 수정 통합 테스트")
    void updateDiaryTest() throws Exception {
        // given
        LocalDate today = LocalDate.now();
        String originalText = "원본 일기";
        String updatedText = "수정된 일기";

        when(weatherService.getWeatherData(any(LocalDate.class)))
                .thenReturn(new WeatherService.WeatherDto("Cloudy", 20.0));

        mockMvc.perform(post("/create/diary")
                        .param("date", today.toString())
                        .param("text", originalText))
                .andExpect(status().isOk());

        mockMvc.perform(put("/update/diary")
                        .param("date", today.toString())
                        .param("text", updatedText))
                .andExpect(status().isOk());

        List<Diary> diaries = diaryRepository.findAllByDate(today);
        assertEquals(1, diaries.size());
        assertEquals(updatedText, diaries.get(0).getText());
    }

    @Test
    @DisplayName("일기 삭제 통합 테스트")
    void deleteDiaryTest() throws Exception {
        // given
        LocalDate today = LocalDate.now();
        String text = "삭제될 일기";

        when(weatherService.getWeatherData(any(LocalDate.class)))
                .thenReturn(new WeatherService.WeatherDto("Rainy", 15.0));

        mockMvc.perform(post("/create/diary")
                        .param("date", today.toString())
                        .param("text", text))
                .andExpect(status().isOk());

        List<Diary> diariesBeforeDelete = diaryRepository.findAllByDate(today);
        assertEquals(1, diariesBeforeDelete.size());

        mockMvc.perform(delete("/delete/diary")
                        .param("date", today.toString()))
                .andExpect(status().isOk());

        List<Diary> diariesAfterDelete = diaryRepository.findAllByDate(today);
        assertEquals(0, diariesAfterDelete.size());
    }

    @Test
    @DisplayName("기간별 일기 조회 통합 테스트")
    void readDiariesByPeriodTest() throws Exception {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(weatherService.getWeatherData(any(LocalDate.class)))
                .thenReturn(new WeatherService.WeatherDto("Clear", 22.0));

        mockMvc.perform(post("/create/diary")
                        .param("date", yesterday.toString())
                        .param("text", "어제의 일기"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/create/diary")
                        .param("date", today.toString())
                        .param("text", "오늘의 일기"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/create/diary")
                        .param("date", tomorrow.toString())
                        .param("text", "내일의 일기"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/read/diaries")
                        .param("startDate", yesterday.toString())
                        .param("endDate", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].text").value("어제의 일기"))
                .andExpect(jsonPath("$[1].text").value("오늘의 일기"));
    }

    @Test
    @DisplayName("존재하지 않는 일기 수정 시 예외 발생 테스트")
    void updateNonExistingDiaryTest() throws Exception {
        // given
        LocalDate futureDate = LocalDate.now().plusYears(1);
        String text = "미래의 일기";

        // when & then
        mockMvc.perform(put("/update/diary")
                        .param("date", futureDate.toString())
                        .param("text", text))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(
                        containsString("해당 날짜의 일기가 존재하지 않습니다")));
    }
}