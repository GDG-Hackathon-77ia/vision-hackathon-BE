package com.gdg.kkia.point.controller;

import com.gdg.kkia.point.dto.PointLogResponse;
import com.gdg.kkia.point.dto.PointResponse;
import com.gdg.kkia.point.service.PointLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 조회", description = "포인트 조회 관련 API")
@RequestMapping("/api/point")
public class PointLogController {

    private final PointLogService pointLogService;

    @Operation(summary = "포인트 로그 조회", description = "로그인한 사용자의 포인트 로그를 조회하여 리스트형태로 받습니다.")
    @GetMapping("/log")
    public ResponseEntity<List<PointLogResponse>> getPointLog(@RequestAttribute("memberId") Long memberId) {
        List<PointLogResponse> pointLogResponses = pointLogService.getPointLogList(memberId);
        return ResponseEntity.ok().body(pointLogResponses);
    }

    @Operation(summary = "포인트 조회", description = "로그인한 사용자의 포인트를 조회합니다.")
    @GetMapping
    public ResponseEntity<PointResponse> getMemberPoint(@RequestAttribute("memberId") Long memberId) {
        return ResponseEntity.ok().body(pointLogService.getMemberPoint(memberId));
    }

}
