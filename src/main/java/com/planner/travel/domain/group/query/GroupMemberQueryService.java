package com.planner.travel.domain.group.query;

import com.planner.travel.domain.group.dto.response.GroupMemberResponse;
import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.entity.QGroupMember;
import com.planner.travel.domain.planner.entity.QPlanner;
import com.planner.travel.domain.profile.entity.QProfile;
import com.planner.travel.domain.user.entity.QUser;
import com.planner.travel.global.util.image.entity.QImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMemberQueryService {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public GroupMemberQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<GroupMemberResponse> findGroupMembersByPlannerId(Long plannerId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;

        List<GroupMember> groupMembers = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.planner.id.eq(plannerId)
                        .and(qGroupMember.isLeaved.isFalse())
                )
                .fetch();

        return groupMembers.stream()
                .map(groupMember -> {
                    String profileImgUrl = groupMember.getUser().getProfile().getImage().getImageUrl();

                    if (profileImgUrl.isEmpty()) {
                        profileImgUrl = "";
                    }

                    return new GroupMemberResponse(
                            groupMember.getId(),
                            groupMember.getUser().getId(),
                            groupMember.getUser().getNickname(),
                            groupMember.getUser().getUserTag(),
                            profileImgUrl,
                            groupMember.isHost()
                    );
                })
                .collect(Collectors.toList());
    }

    public boolean validateGroupMember(Long userId, Long plannerId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;

        Long count = queryFactory
                .select(qGroupMember.count())
                .from(qGroupMember)
                .where(qGroupMember.user.id.eq(userId)
                        .and(qGroupMember.planner.id.eq(plannerId)))
                .fetchOne();

        return count != null && count > 0;
    }

    public GroupMember findGroupMember(Long userId, Long plannerId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;

        GroupMember groupMember = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.user.id.eq(userId)
                        .and(qGroupMember.planner.id.eq(plannerId))
                        .and(qGroupMember.isLeaved.isFalse()))
                .fetchOne();

        return groupMember;
    }

    public int getGroupMemberSize(Long plannerId) {
        QGroupMember qGroupMember = QGroupMember.groupMember;

        List<GroupMember> groupMembers = queryFactory
                .selectFrom(qGroupMember)
                .where(qGroupMember.planner.id.eq(plannerId)
                        .and(qGroupMember.isLeaved.isFalse()))
                .fetch();

        return groupMembers.size();
    }
}
