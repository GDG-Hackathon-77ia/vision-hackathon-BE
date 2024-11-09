package com.gdg.kkia.storage.dailyresponse.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.storage.dailyresponse.dto.DailyQuestionRequest;
import com.gdg.kkia.storage.dailyresponse.dto.DailyQuestionResponse;
import com.gdg.kkia.storage.dailyresponse.service.DailyQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "1일 1질문", description = "1일 1질문 관련 API")
public class DailyQuestionResponseController {

    private final DailyQuestionService dailyQuestionService;

    @Operation(summary = "질문 추가", description = "1일 1질문 서비스의 질문을 추가합니다.")
    @PostMapping("/question")
    public ResponseEntity<StringTypeMessageResponse> addDailyQuestion(DailyQuestionRequest dailyQuestionRequest) {
        dailyQuestionService.addDailyQuestion(dailyQuestionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("질문이 추가되었습니다."));
    }

    @Operation(summary = "랜덤한 하나의 질문 조회", description = "최근 조회된 질문 제외한 랜덤한 하나의 질문을 조회합니다.")
    @GetMapping("/question")
    public ResponseEntity<DailyQuestionResponse> getRandomQuestionExcludingRecent() {
        DailyQuestionResponse dailyQuestionResponse = dailyQuestionService.getRandomQuestionExcludingRecent();
        return ResponseEntity.ok().body(dailyQuestionResponse);
    }

    @Operation(summary = "모든 질문 조회 (관리자용)", description = "1일 1질문 서비스의 모든 질문을 조회합니다.")
    @GetMapping("/question/all")
    public ResponseEntity<List<DailyQuestionResponse>> getAllDailyQuestion() {
        List<DailyQuestionResponse> dailyQuestionResponses = dailyQuestionService.getAllDailyQuestionForManager();
        return ResponseEntity.ok().body(dailyQuestionResponses);
    }

    @Operation(summary = "질문 수정", description = "dailyQuestionId에 해당하는 질문을 수정합니다.")
    @PutMapping("/question/{dailyQuestionId}")
    public ResponseEntity<StringTypeMessageResponse> updateDailyQuestion(@PathVariable Long dailyQuestionId, DailyQuestionRequest dailyQuestionRequest) {
        dailyQuestionService.updateDailyQuestion(dailyQuestionId, dailyQuestionRequest);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("수정되었습니다."));
    }

    @Operation(summary = "질문 삭제", description = "dailyQuestionId에 해당하는 질문을 수정합니다.")
    @DeleteMapping("/question/{dailyQuestionId}")
    public ResponseEntity<StringTypeMessageResponse> deleteDailyQuestion(@PathVariable Long dailyQuestionId) {
        dailyQuestionService.deleteDailyQuestion(dailyQuestionId);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("삭제되었습니다."));
    }
}
