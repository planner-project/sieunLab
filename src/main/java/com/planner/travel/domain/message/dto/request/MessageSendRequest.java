package com.planner.travel.domain.message.dto.request;

public record MessageSendRequest(
        Long friendId,
        Long sendUserId,
        String content
) {
}
