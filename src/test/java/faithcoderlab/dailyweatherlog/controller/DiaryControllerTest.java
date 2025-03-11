package faithcoderlab.dailyweatherlog.controller;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.service.DiaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
public class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiaryService diaryService;

    @Test
    @DisplayName("일기 생성 테스트")
    void createDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String text = "테스트 일기입니다";
        doNothing().when(diaryService).createDiary(any(LocalDate.class), anyString());

        // when & then
        mockMvc.perform(post("/create/diary")
                        .param("date", "2024-12-31")
                        .param("text", text)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(diaryService).createDiary(eq(date), eq(text));
    }

    @Test
    @DisplayName("특정 날짜 일기 조회 테스트")
    void readDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        List<Diary> diaries = new ArrayList<>();
        diaries.add(Diary.builder()
                .id(1L)
                .date(date)
                .text("테스트 일기입니다")
                .weather("Clear")
                .temperature(22.5)
                .build());

        when(diaryService.readDiary(any(LocalDate.class))).thenReturn(diaries);

        // when & then
        mockMvc.perform(get("/read/diary")
                        .param("date", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value("2024-12-31"))
                .andExpect(jsonPath("$[0].text").value("테스트 일기입니다"))
                .andExpect(jsonPath("$[0].weather").value("Clear"))
                .andExpect(jsonPath("$[0].temperature").value(22.5));

        verify(diaryService).readDiary(eq(date));
    }

    @Test
    @DisplayName("기간별 일기 조회 테스트")
    void readDiariesTest() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Diary> diaries = new ArrayList<>();
        diaries.add(Diary.builder()
                .id(1L)
                .date(startDate)
                .text("시작일 일기")
                .weather("Cloudy")
                .temperature(15.0)
                .build());
        diaries.add(Diary.builder()
                .id(2L)
                .date(endDate)
                .text("종료일 일기")
                .weather("Sunny")
                .temperature(25.0)
                .build());

        when(diaryService.readDiaries(any(LocalDate.class), any(LocalDate.class))).thenReturn(diaries);

        // when & then
        mockMvc.perform(get("/read/diaries")
                        .param("startDate", "2024-12-01")
                        .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value("2024-12-01"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].date").value("2024-12-31"));

        verify(diaryService).readDiaries(eq(startDate), eq(endDate));
    }

    @Test
    @DisplayName("일기 수정 테스트")
    void updateDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        String newText = "수정된 일기 내용";
        doNothing().when(diaryService).updateDiary(any(LocalDate.class), anyString());

        // when & then
        mockMvc.perform(put("/update/diary")
                        .param("date", "2024-12-31")
                        .param("text", newText))
                .andExpect(status().isOk());

        verify(diaryService).updateDiary(eq(date), eq(newText));
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 12, 31);
        doNothing().when(diaryService).deleteDiary(any(LocalDate.class));

        // when & then
        mockMvc.perform(delete("/delete/diary")
                        .param("date", "2024-12-31"))
                .andExpect(status().isOk());

        verify(diaryService).deleteDiary(eq(date));
    }

    @Test
    @DisplayName("잘못된 날짜 형식으로 요청시 실패 테스트")
    void invalidDateFormatTest() throws Exception {
        mockMvc.perform(get("/read/diary")
                        .param("date", "2024/12/31"))
                .andExpect(status().is5xxServerError());
    }
}