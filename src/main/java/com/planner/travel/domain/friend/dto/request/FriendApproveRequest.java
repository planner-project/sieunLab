package com.planner.travel.domain.friend.dto.request;

public record FriendApproveRequest(
        Long friendId,
        Long friendUserId,
        Long userId
) {
}
