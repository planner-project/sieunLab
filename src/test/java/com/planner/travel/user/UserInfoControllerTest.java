package com.planner.travel.user;
import com.planner.travel.domain.user.controller.UserInfoController;
import com.planner.travel.domain.user.dto.response.UserInfoResponse;
import com.planner.travel.domain.user.entity.Sex;
import com.planner.travel.domain.user.service.UserInfoService;
import com.planner.travel.global.ApiDocumentUtil;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoController.class)
@WithMockUser(username="wldsmtldsm65@gmail.com", roles={"USER"})
@AutoConfigureRestDocs
public class UserInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private TokenExtractor tokenExtractor;

    @MockBean
    private SubjectExtractor subjectExtractor;

    @Test
    public void testGetUserInfo() throws Exception {
        String fakeToken = "Bearer generated.access.token";
        Long expectedUserId = 1L;
        UserInfoResponse expectedUserInfo = new UserInfoResponse(
                1L,
                "시은",
                1234L,
                LocalDate.parse("1996-11-20"),
                "wldsmtldsm65@gmail.com",
                "",
                false,
                Sex.NONE
        );

        when(tokenExtractor.getAccessTokenFromHeader(any(HttpServletRequest.class))).thenReturn(fakeToken);
        when(subjectExtractor.getUserIdFromToken(fakeToken)).thenReturn(expectedUserId);
        when(userInfoService.get(expectedUserId)).thenReturn(expectedUserInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/user")
                        .header("Authorization", fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(expectedUserInfo.userId()))
                .andExpect(jsonPath("$.nickname").value(expectedUserInfo.nickname()))
                .andExpect(jsonPath("$.userTag").value(expectedUserInfo.userTag()))
                .andExpect(jsonPath("$.birthday").value(expectedUserInfo.birthday().toString()))
                .andExpect(jsonPath("$.email").value(expectedUserInfo.email()))
                .andExpect(jsonPath("$.profileImgUrl").value(expectedUserInfo.profileImgUrl()))
                .andExpect(jsonPath("$.isBirthday").value(expectedUserInfo.isBirthday()))
                .andExpect(jsonPath("$.sex").value(expectedUserInfo.sex().toString()))

                .andDo(MockMvcRestDocumentation.document("userInfo",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("userId").description("유저 인덱스"),
                                PayloadDocumentation.fieldWithPath("nickname").description("유저 닉네임"),
                                PayloadDocumentation.fieldWithPath("userTag").description("유저 태그"),
                                PayloadDocumentation.fieldWithPath("birthday").description("유저 생년월일"),
                                PayloadDocumentation.fieldWithPath("email").description("유저 이메일"),
                                PayloadDocumentation.fieldWithPath("profileImgUrl").description("프로필 이미지 url"),
                                PayloadDocumentation.fieldWithPath("isBirthday").description("유저 생일 여부"),
                                PayloadDocumentation.fieldWithPath("sex").description("유저 성별")
                        )
                ));
    }
}
