package com.gdg.kkia.chatbot.dto;

import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private String question;
    private String response;
    private LocalDateTime responseDateTime;
    private GeminiRequestType type;
}
