package com.planner.travel.domain.friend.dto.response;

public record FriendResponse(
        Long friendId,
        Long friendUserId,
        String nickname,
        Long userTag,
        String profileImageUrl
) {
}