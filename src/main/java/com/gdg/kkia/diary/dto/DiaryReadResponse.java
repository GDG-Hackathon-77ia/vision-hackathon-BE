package com.gdg.kkia.diary.dto;

import com.gdg.kkia.diary.entity.Diary;

import java.time.LocalDateTime;

public record DiaryReadResponse(
        Long diaryId,
        LocalDateTime localDateTime,
        Diary.Type type,
        String content
) {
}
