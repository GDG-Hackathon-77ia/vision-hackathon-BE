package com.gdg.kkia.chatbot.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GeminiContent {
    private String role;
    private List<Part> parts = new ArrayList<>();

    @Builder
    public GeminiContent(String role, String text) {
        this.role = role;
        this.parts.add(new Part(text));
    }

    @Getter
    @NoArgsConstructor
    public static class Part {
        private String text;

        private Part(String text) {
            this.text = text;
        }
    }
}
