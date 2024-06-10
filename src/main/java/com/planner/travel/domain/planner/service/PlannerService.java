package com.planner.travel.domain.planner.service;

import com.planner.travel.domain.planner.dto.response.PlannerResponse;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlannerService {
    private final PlannerQueryService plannerQueryService;

    public PlannerResponse getPlanner(Long plannerId) {
        PlannerResponse plannerResponse = plannerQueryService.findPlannerById(plannerId);
        return plannerResponse;
    }
}
