package com.gdg.kkia.diary.dto;

import com.gdg.kkia.diary.entity.Diary;

import java.time.LocalDateTime;

public record DiaryReadResponse(
        Long diaryId,
        LocalDateTime writtenDateTime,
        Diary.Type type,
        String content
) {
}
