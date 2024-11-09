package com.gdg.kkia.chatbot.dto;

import com.gdg.kkia.chatbot.entity.GeminiContent;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeminiRequest {
    private List<GeminiContent> contents = new ArrayList<>();
    private GenerationConfig generationConfig;

    @Getter @Setter
    public static class GenerationConfig {
        private int candidate_count;
        private int max_output_tokens;
        private double temperature;
    }

    public void addContent(GeminiContent content) {
        this.contents.add(content);
    }
}
