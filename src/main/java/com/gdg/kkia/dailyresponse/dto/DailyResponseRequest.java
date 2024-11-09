package com.gdg.kkia.dailyresponse.dto;

public record DailyResponseRequest(
        Long questionId,
        String response
) {
}
