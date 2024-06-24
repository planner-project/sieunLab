package com.planner.travel.global.util.search.query;

import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.entity.QGroupMember;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.entity.QPlanner;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import com.planner.travel.global.util.PaginationUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlannerSearchService {
    private final JPAQueryFactory queryFactory;
    private final PlannerQueryService plannerQueryService;

    @Autowired
    public PlannerSearchService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.plannerQueryService = new PlannerQueryService(entityManager);
    }

    public Page<PlannerListResponse> searchUnPrivatePlanners(String input, Pageable pageable) {
        QGroupMember qGroupMember = QGroupMember.groupMember;
        QPlanner qPlanner = QPlanner.planner;

        String likePattern = "%" + input + "%";

        List<GroupMember> planners = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.planner.isPrivate.isFalse()
                        .and(qGroupMember.isHost.isTrue())
                        .and(qGroupMember.planner.title.like(likePattern)))
                .orderBy(qPlanner.id.desc())
                .fetch();

        return PaginationUtil.listToPage(plannerQueryService.getPlannerListResponses(planners), pageable);
    }

    public Page<PlannerListResponse> searchMyPlanners(Long userId, String input, Pageable pageable) {
        QGroupMember qGroupMember = QGroupMember.groupMember;
        QPlanner qPlanner = QPlanner.planner;

        String likePattern = "%" + input + "%";

        List<GroupMember> planners = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.user.id.eq(userId)
                        .and(qGroupMember.isLeaved.isFalse())
                        .and(qGroupMember.planner.title.like(likePattern))
                )
                .orderBy(qPlanner.id.desc())
                .fetch();

        return PaginationUtil.listToPage(plannerQueryService.getPlannerListResponses(planners), pageable);
    }
}
