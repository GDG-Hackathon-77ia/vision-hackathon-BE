package com.gdg.kkia.chatbot.dto;

import com.gdg.kkia.chatbot.entity.GeminiContent;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeminiRequest {
    private List<GeminiContent> contents = new ArrayList<>();
    private GenerationConfig generationConfig;

    public void addContent(GeminiContent content) {
        this.contents.add(content);
    }

    @Getter
    @Setter
    public static class GenerationConfig {
        private int candidate_count;
        private int max_output_tokens;
        private double temperature;
        private String response_mime_type;
        private Map<String, Object> response_schema;
    }

    public void setTextResponse() {
        if (this.generationConfig == null) {
            this.generationConfig = new GenerationConfig();
        }
        this.generationConfig.setResponse_mime_type("text/plain");
    }

    public void setJsonResponse(Map<String, Object> schema) {
        if (this.generationConfig == null) {
            this.generationConfig = new GenerationConfig();
        }
        this.generationConfig.setResponse_mime_type("application/json");
        this.generationConfig.setResponse_schema(schema);
    }
}

/*
# Create the model
generation_config = {
  "temperature": 1,
  "top_p": 0.95,
  "top_k": 40,
  "max_output_tokens": 8192,
  "response_mime_type": "application/json",
}
*/
