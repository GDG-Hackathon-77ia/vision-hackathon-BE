package com.gdg.kkia.diary.dto;

import com.gdg.kkia.diary.entity.Diary;

public record DiaryWriteRequest(
        Diary.Type type,
        String content
) {
}
