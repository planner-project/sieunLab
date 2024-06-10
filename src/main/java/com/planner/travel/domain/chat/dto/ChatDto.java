package com.planner.travel.domain.chat.dto;

public record ChatDto(
        Long userId,
        String nickname,
        String profileImgUrl,
        String message
) {
}
