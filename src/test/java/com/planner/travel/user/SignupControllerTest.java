package com.planner.travel.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planner.travel.global.auth.basic.controller.BasicAuthController;
import com.planner.travel.global.auth.basic.dto.response.SignupRequest;
import com.planner.travel.global.auth.basic.service.LoginService;
import com.planner.travel.global.auth.basic.service.SignupService;
import com.planner.travel.global.ApiDocumentUtil;
import com.planner.travel.global.util.CookieUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasicAuthController.class)
@WithMockUser
@AutoConfigureRestDocs
public class SignupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;
    @MockBean
    private LoginService loginService;
    @MockBean
    private CookieUtil cookieUtil;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @DisplayName("회원 가입")
    @Test
    void signup() throws Exception {
        SignupRequest request = new SignupRequest(
                "wldsmtldsm65@gmail.com",
                "123qwe!@#QWE",
                "시니",
                LocalDate.parse("1996-11-20")
        );

        doNothing()
                .when(signupService)
                .signup(any(SignupRequest.class));

        mockMvc
                .perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("signup",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일")

                        )));

        verify(signupService, times(1)).signup(any(SignupRequest.class));
    }


    @DisplayName("이메일 양식 검증")
    @Test
    void validateEmail() throws Exception {
        SignupRequest request = new SignupRequest(
                "invalid-email",
                "123qwe!@#QWE",
                "시니",
                LocalDate.parse("1996-11-20")
        );

        doNothing()
                .when(signupService)
                .signup(any(SignupRequest.class));

        mockMvc
                .perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andDo(MockMvcRestDocumentation.document("signup-invalid-email",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일")

                        )));

        verify(signupService, never()).signup(any(SignupRequest.class));
    }


    @DisplayName("존재 하는 이메일 검증")
    @Test
    void validateExistEmail() throws Exception {
        SignupRequest request = new SignupRequest(
                "wldsmtldsm65@gmail.com",
                "123qwe!@#QWE",
                "시니",
                LocalDate.parse("1996-11-20")
        );

        doThrow(new IllegalArgumentException())
                .when(signupService)
                .signup(any(SignupRequest.class));

        mockMvc
                .perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalArgumentException.class, result.getResolvedException()))
                .andDo(MockMvcRestDocumentation.document("signup-existent-email",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일")

                        )));

        verify(signupService, times(1)).signup(any(SignupRequest.class));
    }

    @DisplayName("닉네임 양식 검증")
    @Test
    // a(길이 미달), 시니Aaaaaaaaaaaaaa(길이 초과), 시니😧(특수 문자 사용)
    void validateNickname() throws Exception {
        SignupRequest request = new SignupRequest(
                "wldsmtldsm65@gmail.com",
                "123qwe!@#QWE",
                "[시니]", // 특수 문자 사용
                LocalDate.parse("1996-11-20")
        );

        doNothing()
                .when(signupService)
                .signup(any(SignupRequest.class));

        mockMvc
                .perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andDo(MockMvcRestDocumentation.document("signup-invalid-nickname",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일")

                        )));

        verify(signupService, never()).signup(any(SignupRequest.class));
    }

    @DisplayName("비밀번호 양식 검증")
    @Test
        // 123456789(길이 만족, 숫자만 사용), aaaaaaaaaa(길이 만족, 소문자만 사용), aaaaaa#####(길이 만족, 소문자, 특수 문자만 사용)
        // , aA12!@#(길이 미달), aA12!@#aaaaaaaaaaaaaaaaaa(길이 초과), aA12!@#aa아a(길이 만족, 한글 사용)
    void validatePassword() throws Exception {
        SignupRequest request = new SignupRequest(
                "wldsmtldsm65@gmail.com",
                "aA12!@#aa아a",
                "시니",
                LocalDate.parse("1996-11-20")
        );

        doNothing()
                .when(signupService)
                .signup(any(SignupRequest.class));

        mockMvc
                .perform(post("/api/v1/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andDo(MockMvcRestDocumentation.document("signup-invalid-password",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일")

                        )));

        verify(signupService, never()).signup(any(SignupRequest.class));
    }
}