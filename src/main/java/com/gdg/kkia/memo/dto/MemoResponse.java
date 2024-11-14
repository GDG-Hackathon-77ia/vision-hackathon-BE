package com.gdg.kkia.memo.dto;

import java.time.LocalDateTime;

public record MemoResponse(
        Long diaryId,
        LocalDateTime writtenDateTime,
        String content
) { }
