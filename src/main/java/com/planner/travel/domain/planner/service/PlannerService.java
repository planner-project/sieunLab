package com.planner.travel.domain.planner.service;

import com.planner.travel.domain.planner.dto.response.PlannerResponse;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlannerService {
    private final PlannerQueryService plannerQueryService;

    private final TokenExtractor tokenExtractor;
    private final SubjectExtractor subjectExtractor;

    public PlannerResponse getPlanner(Long userId, Long plannerId, HttpServletRequest request) {
        String accessToken = tokenExtractor.getAccessTokenFromHeader(request);
        Long loginUserId = subjectExtractor.getUserIdFromToken(accessToken);

        if (loginUserId.equals(userId)) {
            return plannerQueryService.findPlannerById(plannerId, "my");

        } else {
            return plannerQueryService.findPlannerById(plannerId, "other");
        }
    }
}
