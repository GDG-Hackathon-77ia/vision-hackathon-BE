package com.gdg.kkia.diary.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.diary.dto.DiaryReadResponse;
import com.gdg.kkia.diary.dto.DiaryWriteRequest;
import com.gdg.kkia.diary.service.DiaryService;
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
@RequestMapping("/api/diary")
@Tag(name = "일기", description = "일기 관련 API")
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 작성", description = "사용자가 일기를 작성합니다. 리스트형태로 여러개를 넣을 수 있습니다.")
    @PostMapping
    public ResponseEntity<StringTypeMessageResponse> writeDiary(@RequestAttribute("memberId") Long memberId, @RequestBody List<DiaryWriteRequest> diaryWriteRequests) {
        diaryService.writeDiary(memberId, diaryWriteRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("일기가 작성되었습니다."));
    }

    @Operation(summary = "유저의 날짜별 작성 일기 조회", description = "localDate에 해당하는 날짜에 사용자가 작성했던 모든 일기를 조회합니다.")
    @GetMapping("/all/{localDate}")
    public ResponseEntity<List<DiaryReadResponse>> getAllDiary(@RequestAttribute("memberId") Long memberId, @PathVariable("localDate") LocalDate localDate) {
        List<DiaryReadResponse> diaryReadResponses = diaryService.getAllDiaryWrittenByMemberInLocalDate(memberId, localDate);
        return ResponseEntity.ok().body(diaryReadResponses);
    }

    @Operation(summary = "작성한 특정 일기 조회", description = "사용자가 작성했던 일기 중 diaryId에 해당하는 일기를 조회합니다.")
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryReadResponse> getOneDiary(@RequestAttribute("memberId") Long memberId, @PathVariable Long diaryId) {
        DiaryReadResponse diaryReadResponses = diaryService.getOneDiaryWrittenByMember(memberId, diaryId);
        return ResponseEntity.ok().body(diaryReadResponses);
    }

    @Operation(summary = "작성한 일기 수정", description = "사용자가 작성했던 일기 중 diaryId에 해당하는 일기를 수정합니다.")
    @PutMapping("/{diaryId}")
    public ResponseEntity<StringTypeMessageResponse> updateDiary(@RequestAttribute("memberId") Long memberId, @PathVariable Long diaryId, @RequestBody DiaryWriteRequest diaryWriteRequest) {
        diaryService.updateDiaryWrittenByMember(memberId, diaryId, diaryWriteRequest);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("수정되었습니다."));
    }

    @Operation(summary = "작성한 일기 삭제", description = "사용자가 작성했던 일기 중 diaryId에 해당하는 일기를 삭제합니다.")
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<StringTypeMessageResponse> deleteDiary(@RequestAttribute("memberId") Long memberId, @PathVariable Long diaryId) {
        diaryService.deleteDiaryWrittenByMember(memberId, diaryId);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("삭제되었습니다."));
    }
}
