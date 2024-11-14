package com.gdg.kkia.dailyresponse.dto;

public record DailyQuestionResponse(
        Long id,
        String question,
        boolean AnsweredToday,
        String answer
) {
}
