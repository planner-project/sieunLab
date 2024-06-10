package com.planner.travel.domain.planner.dto.response;

import java.time.LocalDate;
import java.util.List;

// 해당 Record 는 하나의 플래너를 들어갔을 때 반환할 dto 입니다.
public record PlannerResponse (
        Long plannerId,
        String title,
        String startDate,
        String endDate,
        boolean isPrivate,

        List<PlanBoxResponse> planBoxResponses
) { }
