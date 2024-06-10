package com.planner.travel.global.util.search.dto;

public record UserSearchResponse(
        Long userId,
        String nickname,
        Long userTag,
        String profileImageUrl
) {
}
