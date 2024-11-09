package com.gdg.kkia.chatbot.controller;

import com.gdg.kkia.chatbot.dto.ChatRequest;
import com.gdg.kkia.chatbot.dto.ChatResponse;
import com.gdg.kkia.chatbot.entity.GeminiJsonResponse;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.service.ChatbotResponseService;
import com.gdg.kkia.chatbot.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "챗봇", description = "챗봇 관련 API")
public class ChatbotResponseController {

    private final GeminiService geminiService;
    private final ChatbotResponseService chatbotResponseService;

    @Operation(summary = "채팅 시작", description = "채팅을 시작 혹은 계속 진행합니다.")
    @PostMapping("/gemini/chat/{type}")
    public ResponseEntity<ChatResponse> startChat(@PathVariable("type") GeminiRequestType type, @RequestBody List<ChatRequest> conversations) {
        return ResponseEntity.ok().body(geminiService.startChat(type, conversations));
    }

    @Operation(summary = "채팅 저장", description = "채팅 기록을 저장합니다.")
    @PostMapping("/chatbot/save/{type}")
    public ResponseEntity<Void> saveChatbotResponse(@RequestAttribute("memberId") Long memberId, @PathVariable("type") GeminiRequestType type, @RequestBody List<ChatRequest> conversations) {
        chatbotResponseService.saveChatbotResponses(memberId, type, conversations);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅 불러오기", description = "특정 날짜의 채팅 기록을 불러옵니다.")
    @GetMapping("/chatbot/get/{type}/{localDateTime}")
    public ResponseEntity<List<ChatRequest>> getChatbotResponses(@RequestAttribute("memberId") Long memberId, @PathVariable("type") GeminiRequestType type, @PathVariable("localDateTime") LocalDateTime localDateTime) {
        return ResponseEntity.ok().body(chatbotResponseService.getChatbotResponses(memberId, type, localDateTime));
    }

    @Operation(summary = "Gemini 자가 진단", description = "Gemini가 스스로 문진 후 결과를 보여줍니다.")
    @GetMapping(value = "/chatbot/selftest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeminiJsonResponse> selfTest(@RequestAttribute("memberId") Long memberId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(geminiService.selfTest(memberId));
    }

}
