package com.gdg.kkia.chatbot.controller;

import com.gdg.kkia.chatbot.entity.GeminiContent;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.service.ChatbotResponseService;
import com.gdg.kkia.chatbot.service.GeminiService;
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
@Tag(name = "챗봇", description = "챗봇 관련 API")
public class ChatbotResponseController {

    private final GeminiService geminiService;
    private final ChatbotResponseService chatbotResponseService;

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

    @Operation(summary = "채팅 저장", description = "채팅 기록을 저장합니다.")
    @PostMapping("/chatbot/save/{type}")
    public ResponseEntity<Void> saveChatbotResponse(@RequestAttribute("memberId") Long memberId, @PathVariable("type") GeminiRequestType type, @RequestBody List<GeminiContent> conversations) {
        chatbotResponseService.saveChatbotResponses(memberId, type, conversations);
        return ResponseEntity.ok().build();
    }

//    @Operation(summary = "채팅 불러오기", description = "채팅 기록을 불러옵니다.")
//    @PostMapping("/chatbot/get")
//    public ResponseEntity<Void> deleteMember(@RequestAttribute("memberId") Long memberId, @RequestAttribute("type")GeminiRequestType type, ) {
//        chatbotResponseService.saveChatbotResponses(memberId, type, conversations);
//        return ResponseEntity.ok().build();
//    }

}
