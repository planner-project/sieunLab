package com.planner.travel.global.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.travel.domain.user.dto.response.UserInfoResponse;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.auth.oauth.entity.CustomOAuth2User;
import com.planner.travel.global.jwt.token.TokenGenerator;
import com.planner.travel.global.jwt.token.TokenType;
import com.planner.travel.global.util.CookieUtil;
import com.planner.travel.global.util.RedisUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private String PRE_FRONT_REDIRECT_URL = "http://localhost:5173";
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String provider = customOAuth2User.getUser().getProvider();
//        String email = getEmailByProvider(provider, customOAuth2User);

        User user = userRepository.findById(customOAuth2User.getUser().getId())
                .orElseThrow(EntityNotFoundException::new);

        if (response.isCommitted()) {
            log.info("============================================================================");
            log.info("Social login response has been successfully sent.");
            log.info("============================================================================");
        }

        log.info("============================================================================");
        log.info("Social login successful. Social type is {}", provider);
        log.info("============================================================================");

        String accessToken = tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(user.getId()));
        String refreshToken = tokenGenerator.generateToken(TokenType.REFRESH, String.valueOf(user.getId()));

        response.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.setCookie("refreshToken", refreshToken, response);
        redisUtil.setData(String.valueOf(user.getId()), refreshToken);

        UserInfoResponse userInfoResponse = new UserInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getUserTag(),
                user.getBirthday(),
                user.getEmail(),
                user.getProfile().getProfileImageUrl(),
                isBirthdayToday(user.getBirthday()),
                user.getSex()
        );

        String frontendRedirectUrl = setRedirectUrl(userInfoResponse, accessToken);

        response.sendRedirect(frontendRedirectUrl);
    }

    public String setRedirectUrl (UserInfoResponse userInfoResponse, String accessToken) {
        String encodedUserId = URLEncoder.encode(String.valueOf(userInfoResponse.userId()), StandardCharsets.UTF_8);
        String encodedNickname = URLEncoder.encode(String.valueOf(userInfoResponse.nickname()), StandardCharsets.UTF_8);
        String encodedUserTag = URLEncoder.encode(String.valueOf(userInfoResponse.userTag()), StandardCharsets.UTF_8);
        String encodedBirthday = URLEncoder.encode(String.valueOf(userInfoResponse.birthday()), StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(String.valueOf(userInfoResponse.email()), StandardCharsets.UTF_8);
        String encodedProfileImgUrl = URLEncoder.encode(String.valueOf(userInfoResponse.profileImgUrl()), StandardCharsets.UTF_8);
        String encodedIsBirthDay = URLEncoder.encode(String.valueOf(userInfoResponse.isBirthday()));
        String encodedSex = URLEncoder.encode(String.valueOf(userInfoResponse.sex()));

        String frontendRedirectUrl = String.format(
                "%s/oauth/callback?token=%s&userId=%s&nickname=%s&userTag=%s&birthday=%s&email=%s&profileImgUrl=%s&isBirthday=%s&sex=%s",
                PRE_FRONT_REDIRECT_URL,
                "Bearer " + accessToken,
                encodedUserId,
                encodedNickname,
                encodedUserTag,
                encodedBirthday,
                encodedEmail,
                encodedProfileImgUrl,
                encodedIsBirthDay,
                encodedSex
        );

        return frontendRedirectUrl;
    }

    private boolean isBirthdayToday(LocalDate birthday) {
        return birthday != null && birthday.getMonth() == LocalDate.now().getMonth() &&
                birthday.getDayOfMonth() == LocalDate.now().getDayOfMonth();
    }
}
