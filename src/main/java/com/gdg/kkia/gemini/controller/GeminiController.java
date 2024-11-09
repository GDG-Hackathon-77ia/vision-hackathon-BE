package com.gdg.kkia.gemini.controller;

import com.gdg.kkia.gemini.GeminiRequestType;
import com.gdg.kkia.gemini.dto.GeminiContent;
import com.gdg.kkia.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeminiController {

    private final GeminiService geminiService;

    @GetMapping("/gemini/chat/{type}")
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
