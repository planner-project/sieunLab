package com.planner.travel.domain.friend.dto.request;

public record FriendRequestRequest(
        Long friendUserId,
        Long userId
) {
}
