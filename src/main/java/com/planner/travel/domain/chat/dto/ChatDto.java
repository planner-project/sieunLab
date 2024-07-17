package com.planner.travel.domain.chat.dto;

public record ChatDto(
        Long userId,
        Long userTag,
        String nickname,
        String profileImgUrl,
        String message
) {
}
