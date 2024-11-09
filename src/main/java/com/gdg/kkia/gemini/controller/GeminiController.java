package com.gdg.kkia.gemini.controller;

import com.gdg.kkia.gemini.dto.GeminiContent;
import com.gdg.kkia.gemini.entity.GeminiRequestType;
import com.gdg.kkia.gemini.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "챗봇 채팅", description = "챗봇 채팅 관련 API")
public class GeminiController {

    private final GeminiService geminiService;

    @Operation(summary = "채팅 시작", description = "Gemini와 채팅을 시작 혹은 계속 진행합니다.")
    @PostMapping("/gemini/chat/{type}")
    public ResponseEntity<GeminiContent> startChat(@PathVariable("type") GeminiRequestType type, @RequestBody List<GeminiContent> conversations) {
        try {
            return ResponseEntity.ok().body(geminiService.startChat(type, conversations));
        } catch (HttpClientErrorException e) {
            GeminiContent content = GeminiContent.builder()
                    .role("model")
                    .text(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(content);
        }
    }
}
