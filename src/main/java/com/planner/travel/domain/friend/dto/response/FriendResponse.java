package com.planner.travel.domain.friend.dto.response;

public record FriendResponse(
        Long userId,
        String nickname,
        Long userTag,
        String profileImageUrl
) {
}