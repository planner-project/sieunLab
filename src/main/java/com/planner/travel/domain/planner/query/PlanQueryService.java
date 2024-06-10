package com.planner.travel.domain.planner.query;

import com.planner.travel.domain.planner.dto.response.PlanResponse;
import com.planner.travel.domain.planner.entity.Plan;
import com.planner.travel.domain.planner.entity.QPlan;
import com.planner.travel.domain.planner.entity.QPlanBox;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanQueryService {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PlanQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /*
        SELECT *
        FROM plan
        JOIN plan_box
        ON plan.id = plan_box.plan_id
        WHERE plan_box.id = ? <- 찾고자 하는 plan 이 있는 plan_box 의 index
            AND plan.isDeleted = 0
        ORDER BY plan.time
    */
    public List<PlanResponse> findPlanByPlanBoxId(Long planBoxId) {
        QPlanBox qPlanBox = QPlanBox.planBox;
        QPlan qPlan = QPlan.plan;

        List<Plan> plans = queryFactory
                .selectFrom(qPlan)
                .join(qPlan.planBox, qPlanBox)
                .where(qPlanBox.id.eq(planBoxId)
                        .and(qPlanBox.isDeleted.isFalse()
                        .and(qPlan.isDeleted.isFalse()))
                )
                .orderBy(qPlan.time.asc())
                .fetch();

        List<PlanResponse> planResponses = plans.stream()
                .map(plan -> new PlanResponse(
                        plan.getId(),
                        plan.isPrivate(),
                        plan.getTitle(),
                        plan.getTime(),
                        plan.getContent(),
                        plan.getAddress()
                ))
                .collect(Collectors.toList());

        return planResponses;
    }
}
