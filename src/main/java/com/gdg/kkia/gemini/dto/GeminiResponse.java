package com.gdg.kkia.gemini.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponse {

    private List<Candidate> candidates;
    private PromptFeedback promptFeedback;

    @Getter
    @Setter
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
        private List<SafetyRating> safetyRatings;

    }

    @Getter @Setter
    @ToString
    public static class Content {
        private List<Parts> parts;
        private String role;

    }

    @Getter @Setter
    @ToString
    public static class Parts {
        private String text;

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