package com.gdg.kkia.point.dto;

import com.gdg.kkia.point.entity.PointLog;

import java.time.LocalDateTime;

public record PointLogResponse(
        LocalDateTime receivedDateTime,
        PointLog.Type type,
        PointLog.Status status,
        int receivedPoint
) {
}
