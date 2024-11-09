package com.gdg.kkia.chatbot.controller;

import com.gdg.kkia.gemini.dto.GeminiContent;
import com.gdg.kkia.gemini.entity.GeminiRequestType;
import com.gdg.kkia.chatbot.service.ChatbotResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "챗봇 응답", description = "챗봇 응답(저장) 관련 API")
public class ChatbotResponseController {

    private final ChatbotResponseService chatbotResponseService;

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
