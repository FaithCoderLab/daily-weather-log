package faithcoderlab.dailyweatherlog.controller;

import faithcoderlab.dailyweatherlog.service.DiaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiaryService diaryService;

    @Test
    void createDiaryTest() throws Exception {
        // given
        LocalDate testDate = LocalDate.of(2020, 1, 1);
        String testText = "Test diary entry";

        doNothing().when(diaryService).createDiary(testDate, testText);

        // when & then
        mockMvc.perform(post("/create/diary")
                        .param("date", "2020-01-01")
                        .param("text", testText))
                .andExpect(status().isOk());

        verify(diaryService).createDiary(testDate, testText);
    }
}