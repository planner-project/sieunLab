package com.planner.travel.domain.planner.service;

import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.query.GroupMemberQueryService;
import com.planner.travel.domain.group.repository.GroupMemberRepository;
import com.planner.travel.domain.planner.dto.request.PlannerCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlannerDeleteRequest;
import com.planner.travel.domain.planner.dto.request.PlannerUpdateRequest;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.entity.Planner;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import com.planner.travel.domain.planner.repository.PlannerRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannerListService {
    private final UserRepository userRepository;
    private final PlannerRepository plannerRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryService groupMemberQueryService;
    private final PlannerQueryService plannerQueryService;

    @Transactional(readOnly = true)
    public List<PlannerListResponse> getAllPlanners(Long userId, boolean isLoginUser) {
        List<PlannerListResponse> plannerListResponses = new ArrayList<>();
        System.out.println("============================================================================");
        System.out.println("isLoginUser? : " + isLoginUser);
        System.out.println("============================================================================");

        if (isLoginUser) {
            plannerListResponses = plannerQueryService.findMyPlannersByUserId(userId);

        } else {
            plannerListResponses = plannerQueryService.findPlannersByUserId(userId);
        }

        return plannerListResponses;
    }

    @Transactional
    public void create(PlannerCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Planner planner = Planner.builder()
                .title(request.title())
                .startDate("")
                .endDate("")
                .isPrivate(request.isPrivate())
                .isDeleted(false)
                .build();

        GroupMember groupMember = GroupMember.builder()
                .planner(planner)
                .isHost(true)
                .isLeaved(false)
                .user(user)
                .build();

        plannerRepository.save(planner);
        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void update(PlannerUpdateRequest request, Long plannerId) {
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new EntityNotFoundException("Planner not found"));

        if (isValid(request.title())) {
            planner.updateTitle(request.title());
        }

        planner.updateIsPrivate(request.isPrivate());
        plannerRepository.save(planner);
    }

    @Transactional
    public void delete(PlannerDeleteRequest request, Long plannerId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMember(request.userId(), plannerId);

        groupMember.updateIsLeaved(true);
        groupMemberRepository.save(groupMember);
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }
}
