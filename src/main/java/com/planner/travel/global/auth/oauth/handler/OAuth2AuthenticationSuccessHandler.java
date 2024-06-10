package com.planner.travel.global.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.auth.oauth.entity.CustomOAuth2User;
import com.planner.travel.global.jwt.token.TokenGenerator;
import com.planner.travel.global.jwt.token.TokenType;
import com.planner.travel.global.util.CookieUtil;
import com.planner.travel.global.util.RedisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String provider = customOAuth2User.getUser().getProvider();
//        String email = getEmailByProvider(provider, customOAuth2User);

        if (response.isCommitted()) {
            log.info("============================================================================");
            log.info("Social login response has been successfully sent.");
            log.info("============================================================================");
        }

        log.info("============================================================================");
        log.info("Social login successful. Social type is {}", provider);
        log.info("============================================================================");

        String accessToken = tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(customOAuth2User.getUser().getId()));
        String refreshToken = tokenGenerator.generateToken(TokenType.REFRESH, String.valueOf(customOAuth2User.getUser().getId()));

        response.setHeader("Authorization", "Bearer " + accessToken);
        cookieUtil.setCookie("refreshToken", refreshToken, response);
        redisUtil.setData(String.valueOf(customOAuth2User.getUser().getId()), refreshToken);

        String frontendRedirectUrl = setRedirectUrl(customOAuth2User);

        response.sendRedirect(frontendRedirectUrl);
    }

    private String setRedirectUrl (CustomOAuth2User customOAuth2User) {
        String encodedUserId = URLEncoder.encode(String.valueOf(customOAuth2User.getUser().getId()), StandardCharsets.UTF_8);
        String encodedNickname = URLEncoder.encode(String.valueOf(customOAuth2User.getUser().getNickname()), StandardCharsets.UTF_8);
        String encodedUserTag = URLEncoder.encode(String.valueOf(customOAuth2User.getUser().getUserTag()), StandardCharsets.UTF_8);
        String encodedProfileImgUrl = URLEncoder.encode(String.valueOf(customOAuth2User.getUser().getProfile().getImage().getImageUrl()), StandardCharsets.UTF_8);

        String frontendRedirectUrl = String.format(
                "%s/oauth/callback?&userId=%s&nickname=%s&userTag=%s&profileImgUrl=%s",
                PRE_FRONT_REDIRECT_URL,encodedUserId,encodedNickname,encodedUserTag, encodedProfileImgUrl
        );

        return frontendRedirectUrl;
    }
}
