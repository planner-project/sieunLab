package com.planner.travel.domain.planner.dto.request;


import jakarta.validation.constraints.Size;

public record PlannerCreateRequest(
        @Size(min = 2, max = 20)
        String title,
        boolean isPrivate) {
}
