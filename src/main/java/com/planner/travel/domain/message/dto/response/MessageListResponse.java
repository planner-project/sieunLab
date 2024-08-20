package com.planner.travel.domain.message.dto.response;

public record MessageListResponse(
        Long friendId,
        String friendNickname,
        String friendProfileImage,
        Long friendTag,
        String recentMessage
) {
}
