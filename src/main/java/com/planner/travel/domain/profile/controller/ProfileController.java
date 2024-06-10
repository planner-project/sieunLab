package com.planner.travel.domain.profile.controller;

import com.planner.travel.domain.profile.dto.request.UserInfoUpdateRequest;
import com.planner.travel.domain.profile.service.ProfileImageService;
import com.planner.travel.domain.profile.service.UserInfoUpdateService;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.service.ImageDeleteService;
import com.planner.travel.global.util.image.service.ImageUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {
    private final UserInfoUpdateService userInfoUpdateService;
    private final ProfileImageService profileImageService;
    private final TokenExtractor tokenExtractor;
    private final SubjectExtractor subjectExtractor;

    @GetMapping(value = "/{userId}")
    public void getProfile(@PathVariable("userId") Long userId) {

    }

    @PatchMapping(value = "/{userId}/image")
    public void updateProfileImage(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "profileImage", required = false) MultipartFile multipartFile) throws Exception {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            profileImageService.update(userId, multipartFile);

        } else {
            profileImageService.delete(userId);
        }
    }

    @PatchMapping(value = "/{userId}/info")
    public void updateUserInfo(@PathVariable("userId") Long userId, @RequestBody UserInfoUpdateRequest request) {
        userInfoUpdateService.update(userId, request);
    }

    @DeleteMapping(value = "")
    public void withdrawal(HttpServletRequest request) {
        String accessToken = tokenExtractor.getAccessTokenFromHeader(request);
        Long userId = subjectExtractor.getUserIdFromToken(accessToken);

        userInfoUpdateService.withdrawal(userId);
    }
}
