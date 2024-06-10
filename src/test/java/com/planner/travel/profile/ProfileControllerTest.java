package com.planner.travel.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planner.travel.domain.profile.dto.request.UserInfoUpdateRequest;
import com.planner.travel.domain.profile.entity.Profile;
import com.planner.travel.domain.profile.repository.ProfileRepository;
import com.planner.travel.domain.profile.service.ProfileImageService;
import com.planner.travel.domain.profile.service.UserInfoUpdateService;
import com.planner.travel.domain.user.entity.Sex;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.ApiDocumentUtil;
import com.planner.travel.global.jwt.token.TokenGenerator;
import com.planner.travel.global.jwt.token.TokenType;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.entity.Image;
import com.planner.travel.global.util.image.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserInfoUpdateService userInfoUpdateService;

    @MockBean
    private ProfileImageService profileImageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;

    private String validAccessToken;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        profileRepository.deleteAll();
        imageRepository.deleteAll();

        Image image = Image.builder()
                .imageUrl("")
                .isDeleted(false)
                .isThumb(false)
                .category(Category.PROFILE)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(image);

        Profile profile = Profile.builder()
                .image(image)
                .build();

        profileRepository.save(profile);

        User user = User.builder()
                .email("wldsmtldsm65@gmail.com")
                .password("123qwe!#QWE")
                .nickname("시은")
                .isWithdrawal(false)
                .birthday(LocalDate.parse("1996-11-20"))
                .signupDate(LocalDateTime.now())
                .userTag(1234L)
                .profile(profile)
                .sex(Sex.NONE)
                .build();

        userRepository.save(user);
        userId = user.getId();

        validAccessToken = "Bearer " + tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(userId));
    }

    @Test
    @DisplayName("유저 회원 탈퇴")
    public void userWithdrawalTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users")
                        .header("Authorization", validAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("userWithdrawal",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse()
                ));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assert user.isWithdrawal();
    }

    @Test
    @DisplayName("유저 정보 수정 - 이미지")
    public void updateUserProfileImageTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "profileImage",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/v1/users/{userId}/image", userId)
                        .file(file)
                        .header("Authorization", validAccessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updateProfileImage",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse()
                ));

        verify(profileImageService).update(eq(userId), any(MultipartFile.class));
    }

    @Test
    @DisplayName("유저 정보 수정 - 이미지 없는 경우")
    public void updateUserProfileImageToBasicTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/image", userId)
                        .header("Authorization", validAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updateProfileImageToBasic",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse()
                ));

        verify(profileImageService).delete(userId);
    }

    @Test
    @DisplayName("유저 정보 수정 - 비밀번호")
    public void updatePassword() throws Exception {
        User beforeUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String beforePassword = beforeUser.getPassword();

        UserInfoUpdateRequest request =new UserInfoUpdateRequest("123qwe!@#!@#", null, null, Sex.NONE);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/info", userId)
                        .header("Authorization", validAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updatePassword",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일"),
                                PayloadDocumentation.fieldWithPath("sex").description("유저 성별")
                        )
                ));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assert !beforePassword.equals(user.getPassword());
    }

    @Test
    @DisplayName("유저 정보 수정 - 닉네임")
    public void updateNickname() throws Exception {
        User beforeUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String beforeNickname = beforeUser.getNickname();

        UserInfoUpdateRequest request = new UserInfoUpdateRequest(null, "수민", null, Sex.NONE);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/info", userId)
                        .header("Authorization", validAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updateNinkname",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일"),
                                PayloadDocumentation.fieldWithPath("sex").description("유저 성별")
                        )
                ));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assert !beforeNickname.equals(user.getNickname());
    }

    @Test
    @DisplayName("유저 정보 수정 - 생년월일")
    public void updateBirthday() throws Exception {
        User beforeUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        LocalDate beforeBirthday = beforeUser.getBirthday();

        UserInfoUpdateRequest request = new UserInfoUpdateRequest(null, null, LocalDate.parse("1998-04-06"), Sex.NONE);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/info", userId)
                        .header("Authorization", validAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updateBirthday",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일"),
                                PayloadDocumentation.fieldWithPath("sex").description("유저 성별")
                        )
                ));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assert !beforeBirthday.equals(user.getBirthday());
    }

    @Test
    @DisplayName("유저 정보 수정 - 성별")
    public void updateSex() throws Exception {
        User beforeUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Sex beforeSex = beforeUser.getSex();

        UserInfoUpdateRequest request = new UserInfoUpdateRequest(null, null, null, Sex.WOMAN);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/info", userId)
                        .header("Authorization", validAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updateSex",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("password").description("영어, 숫자, 특수 문자 포함 8 - 20 자리 비밀번호"),
                                PayloadDocumentation.fieldWithPath("nickname").description("특수 문자를 포함 하지 않는 2-12 자 닉네임"),
                                PayloadDocumentation.fieldWithPath("birthday").description("생년월일"),
                                PayloadDocumentation.fieldWithPath("sex").description("유저 성별")
                        )
                ));

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        assert !beforeSex.equals(user.getSex());
    }
}
