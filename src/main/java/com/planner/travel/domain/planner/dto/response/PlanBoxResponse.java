package com.planner.travel.domain.planner.dto.response;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.*;

public record PlanBoxResponse(
        Long planBoxId,
        @NotNull
        LocalDate planDate,
        List<PlanResponse> planResponses
) {
}
