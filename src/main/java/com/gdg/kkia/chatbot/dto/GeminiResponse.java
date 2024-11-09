package com.gdg.kkia.chatbot.dto;

import com.gdg.kkia.chatbot.entity.GeminiContent;
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
        return this.getCandidates().get(0).getContent().getParts().get(0).getText().toString();
    }
}