package com.planner.travel.domain.message.dto.request;

public record MessagesRequest(
        Long userId,
        Long friendId
) {
}
