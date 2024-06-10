package com.planner.travel.domain.user.controller;

import com.planner.travel.domain.user.dto.response.UserInfoResponse;
import com.planner.travel.domain.user.service.UserInfoService;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    private final TokenExtractor tokenExtractor;
    private final SubjectExtractor subjectExtractor;
    private final UserInfoService userInfoService;

    @GetMapping(value = "/user")
    public ResponseEntity<UserInfoResponse> getUserInfo(HttpServletRequest request){
        String token = tokenExtractor.getAccessTokenFromHeader(request);
        Long userId = subjectExtractor.getUserIdFromToken(token);

        UserInfoResponse userInfo = userInfoService.get(userId);

        return ResponseEntity.ok(userInfo);
    }


}

