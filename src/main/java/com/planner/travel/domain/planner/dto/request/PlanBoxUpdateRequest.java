package com.planner.travel.domain.planner.dto.request;

import java.time.LocalDate;

public record PlanBoxUpdateRequest(
        LocalDate planDate,
        boolean isPrivate) {
}
