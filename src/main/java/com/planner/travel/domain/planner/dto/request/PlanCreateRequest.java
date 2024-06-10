package com.planner.travel.domain.planner.dto.request;

import java.time.LocalTime;

public record PlanCreateRequest(
        boolean isPrivate,
        String title,
        LocalTime time,
        String content,
        String address,
        boolean isDeleted
) {
}
