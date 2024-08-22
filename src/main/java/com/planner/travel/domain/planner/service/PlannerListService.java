package com.planner.travel.domain.planner.service;

import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.query.GroupMemberQueryService;
import com.planner.travel.domain.group.repository.GroupMemberRepository;
import com.planner.travel.domain.planner.dto.request.PlannerCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlannerUpdateRequest;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.entity.Planner;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import com.planner.travel.domain.planner.repository.PlannerRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import com.planner.travel.global.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlannerListService {
    private final UserRepository userRepository;
    private final PlannerRepository plannerRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberQueryService groupMemberQueryService;
    private final PlannerQueryService plannerQueryService;
    private final TokenExtractor tokenExtractor;
    private final SubjectExtractor subjectExtractor;


    @Transactional(readOnly = true)
    public Page<PlannerListResponse> getAllPlanners(Long userId, Pageable pageable, HttpServletRequest request) {
        String accessToken = tokenExtractor.getAccessTokenFromHeader(request);
        Long loginUserId = subjectExtractor.getUserIdFromToken(accessToken);

        if (loginUserId.equals(userId)) {
            return getMyPlanners(userId, pageable);

        } else {
            return getOtherPlanners(userId, pageable);
        }
    }

    private Page<PlannerListResponse> getMyPlanners(Long userId, Pageable pageable) {
        return plannerQueryService.findMyPlannersByUserId(userId, pageable);
    }

    private Page<PlannerListResponse> getOtherPlanners(Long userId, Pageable pageable) {
        return plannerQueryService.findPlannersByUserId(userId, pageable);
    }

    @Transactional
    public void create(PlannerCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Planner planner = Planner.builder()
                .user(user)
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
            planner = planner.withTitle(request.title());
        }

        planner = planner.withIsPrivate(request.isPrivate());
        plannerRepository.save(planner);
    }

    @Transactional
    public void delete(Long userId, Long plannerId) {
        GroupMember groupMember = groupMemberQueryService.findGroupMember(userId, plannerId);

        groupMember = groupMember.withIsLeaved();
        groupMember = groupMemberRepository.save(groupMember);
        groupMemberRepository.save(groupMember);
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }
}
