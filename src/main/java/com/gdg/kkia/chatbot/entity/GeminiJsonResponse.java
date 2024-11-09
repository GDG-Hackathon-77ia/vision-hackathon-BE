package com.gdg.kkia.chatbot.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GeminiJsonResponse {
    private int totalScore;
    private List<Question> question;
    private String summary;

    // getters and setters

    @Getter @Setter
    public static class Question {
        private int num;
        private int score;
        private String reason;

        // getters and setters
    }
}
