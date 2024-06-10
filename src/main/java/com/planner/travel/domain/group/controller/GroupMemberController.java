package com.planner.travel.domain.group.controller;

import com.planner.travel.domain.group.dto.request.GroupMemberAddRequest;
import com.planner.travel.domain.group.dto.request.GroupMemberDeleteRequest;
import com.planner.travel.domain.group.dto.response.GroupMemberResponse;
import com.planner.travel.domain.group.query.GroupMemberQueryService;
import com.planner.travel.domain.group.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class GroupMemberController {
    private final GroupMemberService groupMemberService;
    private final GroupMemberQueryService groupMemberQueryService;

    @GetMapping(value = "/{userId}/planners/{plannerId}/group")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId) {
        List<GroupMemberResponse> groupMemberResponses = groupMemberQueryService.findGroupMembersByPlannerId(plannerId);

        return ResponseEntity.ok(groupMemberResponses);
    }

    @GetMapping(value = "/{userId}/planners/{plannerId}/group/check")
    public ResponseEntity<?> isGroupMember(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId) {
        boolean isGroupMember = groupMemberService.isGroupMember(userId, plannerId);

        return ResponseEntity.ok(isGroupMember);
    }

    @PostMapping(value = "/{userId}/planners/{plannerId}/group")
    public ResponseEntity<List<GroupMemberResponse>> addGroupMember(@PathVariable("plannerId") Long plannerId, @RequestBody GroupMemberAddRequest request) {
        groupMemberService.addGroupMembers(plannerId, request);
        List<GroupMemberResponse> groupMemberResponses = groupMemberService.getAllGroupMembers(plannerId);

        return ResponseEntity.ok(groupMemberResponses);
    }

    @PatchMapping(value = "/{userId}/planners/{plannerId}/group")
    public ResponseEntity<List<GroupMemberResponse>> deleteGroupMember(@PathVariable("plannerId") Long plannerId, @RequestBody GroupMemberDeleteRequest request) {
        groupMemberService.deleteGroupMembers(request);
        List<GroupMemberResponse> groupMemberResponses = groupMemberService.getAllGroupMembers(plannerId);

        return ResponseEntity.ok(groupMemberResponses);
    }
}
