package com.planner.travel.domain.planner.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record PlanResponse(
    Long planId,
    boolean isPrivate,
    String title,
    LocalTime time,
    String content,
    String address
) { }
