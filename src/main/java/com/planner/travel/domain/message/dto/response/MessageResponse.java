package com.planner.travel.domain.message.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        Long MessageId,
        boolean isSent,

        Long sendUserId,
        String sendUserNickname,
        Long sendUserTag,
        String sendUserProfileImage,

        Long receivedUserId,
        String receivedUserNickname,
        Long receivedUserTag,
        String receivedUserProfileImage,

        String content,
        LocalDateTime sendAt
) {
}
