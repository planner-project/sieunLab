package com.planner.travel.domain.planner.dto.request;

import java.time.LocalDate;

public record PlanBoxCreateRequest(LocalDate planDate, boolean isPrivate) {
}
