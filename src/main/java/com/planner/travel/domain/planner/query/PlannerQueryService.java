package com.planner.travel.domain.planner.query;

import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.entity.QGroupMember;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.dto.response.PlannerResponse;
import com.planner.travel.domain.planner.entity.Planner;
import com.planner.travel.domain.planner.entity.QPlanBox;
import com.planner.travel.domain.planner.entity.QPlanner;
import com.planner.travel.domain.profile.entity.QProfile;
import com.planner.travel.domain.user.entity.QUser;
import com.planner.travel.global.util.image.entity.QImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlannerQueryService {
    private final JPAQueryFactory queryFactory;
    private final PlanBoxQueryService planBoxQueryService;

    @Autowired
    public PlannerQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.planBoxQueryService = new PlanBoxQueryService(entityManager);
    }

    public List<PlannerListResponse> findMyPlannersByUserId(Long userId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;
        QPlanner qPlanner = QPlanner.planner;

        List<GroupMember> planners = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.user.id.eq(userId)
                        .and(qGroupMember.isLeaved.isFalse())
                )
                .orderBy(qPlanner.id.desc())
                .fetch();

        return getPlannerListResponses(planners);
    }

    public List<PlannerListResponse> findPlannersByUserId(Long userId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;
        QPlanner qPlanner = QPlanner.planner;

        List<GroupMember> planners = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.user.id.eq(userId)
                        .and(qGroupMember.planner.isPrivate.isFalse())
                        .and(qGroupMember.isLeaved.isFalse()
                ))
                .orderBy(qPlanner.id.desc())
                .fetch();

        return getPlannerListResponses(planners);
    }

    @NotNull
    private List<PlannerListResponse> getPlannerListResponses(List<GroupMember> planners) {
        return planners.stream()
                .map(planner -> {
                    String startDate = planner.getPlanner().getStartDate();
                    String endDate = planner.getPlanner().getEndDate();

                    List<String> profileImages = new ArrayList<>();
                    List<GroupMember> groupMembers = planners.stream().toList();

                    int lastId = groupMembers.size();
                    if (lastId > 3) {
                        lastId = 3;
                    }

                    for (int i = 0; i < lastId; i++) {
                        String profileImgUrl = groupMembers.get(i).getUser().getProfile().getImage().getImageUrl();

                        if (!profileImgUrl.isEmpty()) {
                            profileImgUrl = "";
                        }

                        profileImages.add(profileImgUrl);
                    }

                    if (planner.getPlanner().getStartDate() == null) {
                        startDate = "";
                    }

                    if (planner.getPlanner().getEndDate() == null) {
                        endDate = "";
                    }

                    return new PlannerListResponse(
                            planner.getPlanner().getId(),
                            planner.getPlanner().getTitle(),
                            startDate,
                            endDate,
                            planner.getPlanner().isPrivate(),
                            profileImages
                    );
                })
                .collect(Collectors.toList());
    }

    public PlannerResponse findPlannerById(Long plannerId) {
        QPlanner qPlanner = QPlanner.planner;
        String startDate = "";
        String endDate = "";

        Planner planner = queryFactory
                .selectFrom(qPlanner)
                .where(qPlanner.id.eq(plannerId)
                        .and(qPlanner.isDeleted.isFalse())
                )
                .fetchOne();

        if (!planner.getStartDate().isEmpty()) {
            startDate = planner.getStartDate();
        }

        if (!planner.getEndDate().isEmpty()) {
            endDate = planner.getEndDate();
        }

        return new PlannerResponse(
                planner.getId(),
                planner.getTitle(),
                startDate,
                endDate,
                planner.isPrivate(),
                planBoxQueryService.findPlanBoxesByPlannerId(planner.getId())
        );
    }

    public String getStartDate(Long plannerId) {
        QPlanBox qPlanBox = QPlanBox.planBox;
        String minDate = "";

        LocalDate minLocalDate = queryFactory
                .select(qPlanBox.planDate.min())
                .from(qPlanBox)
                .where(qPlanBox.planner.id.eq(plannerId))
                .fetchOne();

        if(minLocalDate != null) {
            minDate = String.valueOf(minLocalDate);
        }

        return minDate;
    }

    public String getEndDate(Long plannerId) {
        QPlanBox qPlanBox = QPlanBox.planBox;
        String maxDate = "";

        LocalDate maxLocalDate = queryFactory
                .select(qPlanBox.planDate.max())
                .from(qPlanBox)
                .where(qPlanBox.planner.id.eq(plannerId))
                .fetchOne();

        if(maxLocalDate != null) {
            maxDate = String.valueOf(maxLocalDate);
        }

        return maxDate;
    }
}
