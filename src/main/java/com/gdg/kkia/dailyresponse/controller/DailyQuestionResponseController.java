package com.gdg.kkia.dailyresponse.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.dailyresponse.dto.DailyQuestionRequest;
import com.gdg.kkia.dailyresponse.dto.DailyQuestionResponse;
import com.gdg.kkia.dailyresponse.dto.DailyResponseRequest;
import com.gdg.kkia.dailyresponse.dto.DailyResponseResponse;
import com.gdg.kkia.dailyresponse.service.DailyQuestionService;
import com.gdg.kkia.dailyresponse.service.DailyResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "1일 1질문", description = "1일 1질문 관련 API")
public class DailyQuestionResponseController {

    private final DailyQuestionService dailyQuestionService;
    private final DailyResponseService dailyResponseService;

    @Operation(summary = "질문 추가 (관리자용)", description = "1일 1질문 서비스의 질문을 추가합니다.")
    @PostMapping("/question")
    public ResponseEntity<StringTypeMessageResponse> addDailyQuestion(@RequestBody DailyQuestionRequest dailyQuestionRequest) {
        dailyQuestionService.addDailyQuestion(dailyQuestionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("질문이 추가되었습니다."));
    }

    @Operation(summary = "랜덤한 하나의 질문 조회", description = "최근 조회된 질문 제외한 랜덤한 하나의 질문을 조회합니다. 사용자의 답변 여부도 함께 리턴되며, 만약 사용자의 답변이 있을 시 답변도 리턴됩니다.")
    @GetMapping("/question")
    public ResponseEntity<DailyQuestionResponse> getRandomQuestionExcludingRecent(@RequestAttribute("memberId") Long memberId) {
        DailyQuestionResponse dailyQuestionResponse = dailyQuestionService.getRandomQuestionExcludingRecent(memberId);
        return ResponseEntity.ok().body(dailyQuestionResponse);
    }

    @Operation(summary = "모든 질문 조회 (관리자용)", description = "1일 1질문 서비스의 모든 질문을 조회합니다.")
    @GetMapping("/question/all")
    public ResponseEntity<List<DailyQuestionResponse>> getAllDailyQuestion() {
        List<DailyQuestionResponse> dailyQuestionResponses = dailyQuestionService.getAllDailyQuestionForManager();
        return ResponseEntity.ok().body(dailyQuestionResponses);
    }

    @Operation(summary = "질문 수정 (관리자용)", description = "dailyQuestionId에 해당하는 질문을 수정합니다.")
    @PutMapping("/question/{dailyQuestionId}")
    public ResponseEntity<StringTypeMessageResponse> updateDailyQuestion(@PathVariable Long dailyQuestionId, @RequestBody DailyQuestionRequest dailyQuestionRequest) {
        dailyQuestionService.updateDailyQuestion(dailyQuestionId, dailyQuestionRequest);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("수정되었습니다."));
    }

    @Operation(summary = "질문 삭제 (관리자용)", description = "dailyQuestionId에 해당하는 질문을 수정합니다.")
    @DeleteMapping("/question/{dailyQuestionId}")
    public ResponseEntity<StringTypeMessageResponse> deleteDailyQuestion(@PathVariable Long dailyQuestionId) {
        dailyQuestionService.deleteDailyQuestion(dailyQuestionId);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("삭제되었습니다."));
    }

    @Operation(summary = "답변 등록", description = "질문에 대한 답변을 등록합니다.")
    @PostMapping("/response")
    public ResponseEntity<StringTypeMessageResponse> addDailyResponse(@RequestAttribute("memberId") Long memberId, @RequestBody DailyResponseRequest dailyResponseRequest) {
        dailyResponseService.saveResponseOfDailyQuestion(memberId, dailyResponseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("답변이 등록되었습니다."));
    }

    @Operation(summary = "본인이 쓴 특정 답변 조회", description = "본인이 작성한 dailyResponseId에 해당하는 답변을 조회합니다.")
    @GetMapping("/response/{dailyResponseId}")
    public ResponseEntity<DailyResponseResponse> getOneResponseOfDailyQuestion(@RequestAttribute("memberId") Long memberId, @PathVariable Long dailyResponseId) {
        DailyResponseResponse dailyResponseResponse = dailyResponseService.getOneResponseOfDailyQuestion(memberId, dailyResponseId);
        return ResponseEntity.ok().body(dailyResponseResponse);
    }

    @Operation(summary = "날짜별 본인이 모든 답변 조회", description = "localDate에 본인이 작성한 모든 답변을 조회합니다.")
    @GetMapping("/response/all/{localDate}")
    public ResponseEntity<List<DailyResponseResponse>> getAllResponseOfDailyQuestion(@RequestAttribute("memberId") Long memberId, @PathVariable("localDate") LocalDate localDate) {
        List<DailyResponseResponse> dailyResponseResponses = dailyResponseService.getAllResponseOfDailyQuestionInLocalDate(memberId, localDate);
        return ResponseEntity.ok().body(dailyResponseResponses);
    }

    @Operation(summary = "본인이 작성한 답변 삭제", description = "본인이 작성한 답변을 삭제합니다.")
    @DeleteMapping("/response/{dailyResponseId}")
    public ResponseEntity<StringTypeMessageResponse> deleteResponseOfDailyQuestion(@RequestAttribute("memberId") Long memberId, @PathVariable Long dailyResponseId) {
        dailyResponseService.deleteResponseOfDailyQuestion(memberId, dailyResponseId);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("삭제되었습니다."));
    }
}
