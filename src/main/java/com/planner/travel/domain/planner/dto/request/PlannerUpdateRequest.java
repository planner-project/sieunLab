package com.planner.travel.domain.planner.dto.request;

public record PlannerUpdateRequest(
    String title,
    boolean isPrivate) {
}
