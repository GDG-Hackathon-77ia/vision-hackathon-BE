package com.gdg.kkia.survey.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.survey.dto.SurveyRequest;
import com.gdg.kkia.survey.dto.SurveyResponse;
import com.gdg.kkia.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
@Tag(name = "진단표 작성", description = "진단표 작성 관련 API")
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "진단표 작성 결과 저장", description = "사용자가 작성한 진단표 작성 결과를 저장합니다.")
    @PostMapping
    public ResponseEntity<StringTypeMessageResponse> saveSurveyWrittenByMember(@RequestAttribute("memberId") Long memberId, @RequestBody SurveyRequest surveyRequest) {
        surveyService.saveSurveyAnswerWrittenByUser(memberId, surveyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("저장되었습니다."));
    }

    @Operation(summary = "사용자가 작성한 진단표 결과 모두 조회", description = "사용자가 작성했던 모든 진단표 결과를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SurveyResponse>> getAllAnswersWrittenByMember(@RequestAttribute("memberId") Long memberId) {
        List<SurveyResponse> surveyResponses = surveyService.getAllSurveyAnswerWrittenByUser(memberId);
        return ResponseEntity.ok().body(surveyResponses);
    }

    @Operation(summary = "인공지능이 작성한 가장 최근의 진단표 결과 조회", description = "인공지능이 작성한 가장 최근의 진단표 결과를 조회합니다.")
    @GetMapping("/ai")
    public ResponseEntity<SurveyResponse> getMostRecentlyWrittenSurveyWrittenByModel(@RequestAttribute("memberId") Long memberId) {
        SurveyResponse surveyResponses = surveyService.getMostRecentlyWrittenSurveyWrittenByModel(memberId);
        return ResponseEntity.ok().body(surveyResponses);
    }
}
