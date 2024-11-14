package com.gdg.kkia.pet.dto;

import com.gdg.kkia.point.entity.PointLog;

public record PointAndPointLogType(
        int point,
        PointLog.Type pointLogType
) {
}
