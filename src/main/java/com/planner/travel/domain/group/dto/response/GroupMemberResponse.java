package com.planner.travel.domain.group.dto.response;

public record GroupMemberResponse(
        Long groupMemberId,
        Long userId,
        String nickname,
        Long userTag,
        String profileImageUrl,
        boolean isHost
) { }
