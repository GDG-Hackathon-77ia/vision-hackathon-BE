package com.gdg.kkia.chatbot.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GeminiContent {
    private String role;
    private List<Part> parts = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    public static class Part {
        private String text;

        private Part(String text) {
            this.text = text;
        }
    }

    @Builder
    public GeminiContent(String role, String text) {
        this.role = role;
        this.parts.add(new Part(text));
    }
}
