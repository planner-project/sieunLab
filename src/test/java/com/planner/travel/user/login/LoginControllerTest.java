package com.planner.travel.user.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.travel.global.auth.basic.controller.BasicAuthController;
import com.planner.travel.global.auth.basic.dto.request.LoginRequest;
import com.planner.travel.global.auth.basic.service.LoginService;
import com.planner.travel.global.auth.basic.service.SignupService;
import com.planner.travel.global.ApiDocumentUtil;
import com.planner.travel.global.jwt.token.TokenGenerator;
import com.planner.travel.global.jwt.token.TokenType;
import com.planner.travel.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasicAuthController.class)
@AutoConfigureRestDocs
@WithMockUser(username="wldsmtldsm65@gmail.com", roles={"USER"})
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;
    @MockBean
    private LoginService loginService;
    @MockBean
    private CookieUtil cookieUtil;
    @MockBean
    private TokenGenerator tokenGenerator;


    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        LoginRequest request = new LoginRequest("wldsmtldsm65@gmail.com", "123qwe!@#QWE");
        String fakeAccessToken = "Bearer valid_access_token";  // 유효한 토큰으로 가정
        String fakeRefreshToken = "valid_refresh_token";  // 유효한 토큰으로 가정

        when(tokenGenerator.generateToken(TokenType.ACCESS, "1")).thenReturn(fakeAccessToken);
        when(tokenGenerator.generateToken(TokenType.REFRESH, "1")).thenReturn(fakeRefreshToken);

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.setHeader("Authorization", fakeAccessToken);

            Cookie cookie = new Cookie("refreshToken", fakeRefreshToken);
            cookie.setPath("/");  // 쿠키 경로 설정
            cookie.setHttpOnly(true);  // HttpOnly 설정

            response.addCookie(cookie);
            return response;

        }).when(loginService).login(any(), any(HttpServletResponse.class));

        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("login",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("Access token 을 포함한 헤더 입니다."),
                                headerWithName("Set-Cookie").description("Refresh token 을 포함한 헤더 입니다.")
                        )))
                .andReturn().getResponse();

        assertEquals(fakeAccessToken, response.getHeader("Authorization"));
        assertTrue(response.getCookies()[0].getValue().contains(fakeRefreshToken));
    }
}
