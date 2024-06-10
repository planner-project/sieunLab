package com.planner.travel.global.auth.basic.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SignupRequest(
        @Email
        String email,

        @Pattern(regexp = "^[A-Za-z\\d~!@#$%^&*()_\\-+=\\[\\]{}|\\\\;:'\",.<>?/]{8,20}$")
        String password,

        @Pattern(regexp = "^[a-zA-Z가-힣\\d]+$")
        @Size(min = 2, max = 12)
        String nickname,

        LocalDate birthday
) {}
