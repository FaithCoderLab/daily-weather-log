package faithcoderlab.dailyweatherlog.controller;

import faithcoderlab.dailyweatherlog.model.Diary;
import faithcoderlab.dailyweatherlog.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Diary API", description = "날씨 일기 관련 API")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/create/diary")
    @Operation(summary = "일기 생성", description = "날짜와 일기 텍스트를 입력받아 날씨 정보와 함께 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 일기가 생성됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> createDiary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam String text
    ) {
        log.info("Request to create diary for date: {}", date);
        diaryService.createDiary(date, text);
        return ResponseEntity.ok().build();
    }

    @GetMapping("read/diary")
    @Operation(summary = "날짜별 일기 조회", description = "특정 날짜의 모든 일기를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 일기 조회됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<Diary>> readDiary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        log.info("Request to read diary for date: {}", date);
        List<Diary> diaries = diaryService.readDiary(date);
        return ResponseEntity.ok(diaries);
    }
}
