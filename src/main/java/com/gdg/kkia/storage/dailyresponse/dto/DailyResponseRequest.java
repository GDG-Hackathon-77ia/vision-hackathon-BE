package com.gdg.kkia.storage.dailyresponse.dto;

public record DailyResponseRequest(
        Long questionId,
        String response
) {
}
