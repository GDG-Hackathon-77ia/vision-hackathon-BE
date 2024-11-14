package com.gdg.kkia.memo.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.memo.dto.MemoRequest;
import com.gdg.kkia.memo.dto.MemoResponse;
import com.gdg.kkia.memo.service.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "메모", description = "메모 관련 API")
public class MemoController {

    private final MemoService memoService;

    @Operation(summary = "메모 작성", description = "사용자가 메모를 작성합니다.")
    @PostMapping("/memo")
    public ResponseEntity<StringTypeMessageResponse> writeDiary(@RequestAttribute("memberId") Long memberId, @RequestBody MemoRequest memoRequest) {
        memoService.writeMemo(memberId, memoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("메모가 작성되었습니다."));
    }

    @Operation(summary = "메모 가져오기", description = "특정 날짜의 메모를 가져옵니다.")
    @GetMapping("/memo/{localDateTime}")
    public ResponseEntity<List<MemoResponse>> getAllMemoByLocalDateTime(@RequestAttribute("memberId") Long memberId, @PathVariable("localDateTime") LocalDateTime localDateTime) {
        return ResponseEntity.ok().body(memoService.getAllMemoByLocalDateTime(memberId, localDateTime));
    }

    @Operation(summary = "메모 수정", description = "사용자가 작성했던 메모 중 memoId에 해당하는 메모를 수정합니다.")
    @PutMapping("/memo/{memoId}")
    public ResponseEntity<StringTypeMessageResponse> updateMemo(@RequestAttribute("memberId") Long memberId, @PathVariable("memoId") Long memoId, @RequestBody MemoRequest memoRequest) {
        memoService.updateMemo(memberId, memoId, memoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("메모가 수정되었습니다."));
    }

    @Operation(summary = "메모 삭제", description = "사용자가 작성했던 메모 중 memoId에 해당하는 메모를 삭제합니다.")
    @DeleteMapping("/memo/{memoId}")
    public ResponseEntity<StringTypeMessageResponse> deleteMemo(@RequestAttribute("memberId") Long memberId, @PathVariable("memoId") Long memoId) {
        memoService.deleteMemo(memberId, memoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("메모가 삭제되었습니다."));
    }
}
