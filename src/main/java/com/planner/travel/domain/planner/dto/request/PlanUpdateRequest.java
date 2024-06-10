package com.planner.travel.domain.planner.dto.request;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record PlanUpdateRequest(
        boolean isPrivate,
        String title,
        LocalTime time,
        String content,
        String address
) {
}
