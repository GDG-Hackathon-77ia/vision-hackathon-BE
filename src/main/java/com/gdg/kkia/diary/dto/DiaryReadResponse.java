package com.gdg.kkia.diary.dto;

import java.time.LocalDateTime;

public record DiaryReadResponse(
        Long diaryId,
        LocalDateTime localDateTime,
        String content
) {
}
