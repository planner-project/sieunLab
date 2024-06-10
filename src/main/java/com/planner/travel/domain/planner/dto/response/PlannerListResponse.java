package com.planner.travel.domain.planner.dto.response;

import java.time.LocalDate;
import java.util.List;

// 나중에 형식이 바뀔 수도 있지만 일단 이렇게 하겠 습니다.
// 플래너 생성과 업데이트에 사용 해 주세요.
public record PlannerListResponse(
        Long plannerId,
        String title,
        String startDate,
        String endDate,
        boolean isPrivate,
        List<String> profileImages
) {
}
