package com.planner.travel.user.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.travel.global.auth.basic.dto.request.LoginRequest;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.auth.basic.service.LoginService;
import com.planner.travel.global.ApiDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class LoginAuthenticationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginService loginService;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("wldsmtldsm65@gmail.com")
                .password("123qwe!#QWE")
                .nickname("시은")
                .isWithdrawal(false)
                .birthday(LocalDate.parse("1996-11-20"))
                .signupDate(LocalDateTime.now())
                .provider("basic")
                .userTag(1234L)
                .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("존재 하지 않는 이메일을 입력한 경우")
    void loginWithWrongEmail() throws Exception {
        LoginRequest request = new LoginRequest(
                "suminnnn@gmail.com",
                "123qwe!@#QWE"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("AUTH_01"))
                .andDo(MockMvcRestDocumentation.document("login-notexistent-email",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호")
                        )
                ));
    }

    @Test
    @DisplayName("잘못된 비밀번호를 입력하여 로그인")
    void loginWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest(
                "wldsmtldsm65@gmail.com",
                "123qwe!@#QWE1"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("AUTH_02"))
                .andDo(MockMvcRestDocumentation.document("login-wrong-password",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("email").description("이메일"),
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호")
                        )
                ));
    }
}