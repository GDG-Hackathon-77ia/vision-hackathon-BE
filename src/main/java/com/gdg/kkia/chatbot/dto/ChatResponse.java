package com.gdg.kkia.chatbot.dto;

import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String response;
    private GeminiRequestType type;
}
