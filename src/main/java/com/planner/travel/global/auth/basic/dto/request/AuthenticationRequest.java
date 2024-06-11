package com.planner.travel.global.auth.basic.dto.request;

public record AuthenticationRequest(
        String email,
        String tempCode
) {
}
