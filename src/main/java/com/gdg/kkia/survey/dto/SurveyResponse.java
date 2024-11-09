package com.gdg.kkia.survey.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyResponse(
        Long surveyId,
        LocalDateTime writtenDateTime,
        List<Integer> answer
) {
}
