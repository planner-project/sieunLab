package com.planner.travel.domain.group.service;

import com.planner.travel.domain.group.dto.request.GroupMemberAddRequest;
import com.planner.travel.domain.group.dto.request.GroupMemberDeleteRequest;
import com.planner.travel.domain.group.dto.response.GroupMemberResponse;
import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.query.GroupMemberQueryService;
import com.planner.travel.domain.group.repository.GroupMemberRepository;
import com.planner.travel.domain.planner.entity.Planner;
import com.planner.travel.domain.planner.repository.PlannerRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberQueryService groupMemberQueryService;
    private final PlannerRepository plannerRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getAllGroupMembers(Long plannerId) {
        List<GroupMemberResponse> groupMemberResponses = groupMemberQueryService.findGroupMembersByPlannerId(plannerId);

        return groupMemberResponses;
    }

    @Transactional(readOnly = true)
    public boolean isGroupMember(Long userId, Long plannerId) {
        boolean isGroupMember = groupMemberQueryService.validateGroupMember(userId, plannerId);

        return isGroupMember;
    }

    @Transactional
    public void addGroupMembers(Long plannerId, GroupMemberAddRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new EntityNotFoundException("Planner not found"));

        if (validateGroupMemberSize(plannerId)) {
            GroupMember groupMember = groupMemberQueryService.findGroupMember(request.userId(), plannerId);

            if (groupMember == null) {
                GroupMember newGroupMember = GroupMember.builder()
                        .isHost(false)
                        .isLeaved(false)
                        .user(user)
                        .planner(planner)
                        .build();

                groupMemberRepository.save(newGroupMember);

            } else {
                groupMember.updateIsLeaved(false);

                groupMemberRepository.save(groupMember);
            }
        }
    }

    @Transactional
    public void deleteGroupMembers(GroupMemberDeleteRequest request) {
        GroupMember groupMember = groupMemberRepository.findById(request.groupMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Group member not found"));

        groupMember.updateIsLeaved(true);
    }

    private boolean validateGroupMemberSize(Long plannerId) {
        int size = groupMemberQueryService.getGroupMemberSize(plannerId);
        if (size > 10) {
            throw new IllegalArgumentException("Group size must be under 10");
        }

        return true;
    }
}
