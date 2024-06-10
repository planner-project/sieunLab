package com.planner.travel.domain.planner.query;

import com.planner.travel.domain.planner.dto.response.PlanBoxResponse;
import com.planner.travel.domain.planner.entity.PlanBox;
import com.planner.travel.domain.planner.entity.QPlanBox;
import com.planner.travel.domain.planner.entity.QPlanner;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanBoxQueryService {
    private final JPAQueryFactory queryFactory;
    private final PlanQueryService planQueryService;

    @Autowired
    public PlanBoxQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.planQueryService = new PlanQueryService(entityManager);
    }

    public List<PlanBoxResponse> findPlanBoxesByPlannerId(Long plannerId) {
        QPlanner qPlanner = QPlanner.planner;
        QPlanBox qPlanBox = QPlanBox.planBox;

        List<PlanBox> planBoxes = queryFactory
                .selectFrom(qPlanBox)
                .join(qPlanBox.planner, qPlanner)
                .where(qPlanner.id.eq(plannerId)
                        .and(qPlanBox.isDeleted.isFalse())
                )
                .orderBy(qPlanBox.planDate.asc())
                .fetch();

        List<PlanBoxResponse> planBoxResponses = planBoxes.stream()
                .map(planBox -> new PlanBoxResponse(
                        planBox.getId(),
                        planBox.getPlanDate(),
                        planQueryService.findPlanByPlanBoxId(planBox.getId())
                ))
                .collect(Collectors.toList());

        return planBoxResponses;
    }
}
