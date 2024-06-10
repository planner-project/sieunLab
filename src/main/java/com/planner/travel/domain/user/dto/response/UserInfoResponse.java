package com.planner.travel.domain.user.dto.response;

import com.planner.travel.domain.user.entity.Sex;

import java.time.LocalDate;

public record UserInfoResponse(
        Long userId,
        String nickname,
        Long userTag,
        LocalDate birthday,
        String email,
        String profileImgUrl,
        boolean isBirthday,
        Sex sex
        ) {
}
