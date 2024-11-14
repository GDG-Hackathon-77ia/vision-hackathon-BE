package com.gdg.kkia.chatbot.entity;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponse {

    private List<Candidate> candidates;
    private PromptFeedback promptFeedback;

    @Getter @Setter
    public static class Candidate {
        private GeminiContent content;
        private String finishReason;
        private int index;
        private List<SafetyRating> safetyRatings;
    }

    @Getter @Setter
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Getter @Setter
    public static class PromptFeedback {
        private List<SafetyRating> safetyRatings;
    }

    public String getResponseText() {
        if (candidates != null && !candidates.isEmpty()) {
            return candidates.get(0).getContent().getParts().get(0).getText();
        }
        return "empty";
    }

    public String getJsonResponse() {
        if (candidates != null && !candidates.isEmpty()) {
            String jsonText = candidates.get(0).getContent().getParts().get(0).getText();
            // JSON 문자열을 파싱하거나 그대로 반환할 수 있습니다.
            return jsonText;
        }
        return null;
    }
}