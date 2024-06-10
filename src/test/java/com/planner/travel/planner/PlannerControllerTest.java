package com.planner.travel.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.repository.GroupMemberRepository;
import com.planner.travel.domain.planner.dto.request.PlannerCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlannerDeleteRequest;
import com.planner.travel.domain.planner.dto.request.PlannerUpdateRequest;
import com.planner.travel.domain.planner.entity.Plan;
import com.planner.travel.domain.planner.entity.PlanBox;
import com.planner.travel.domain.planner.entity.Planner;
import com.planner.travel.domain.planner.repository.PlanBoxRepository;
import com.planner.travel.domain.planner.repository.PlanRepository;
import com.planner.travel.domain.planner.repository.PlannerRepository;
import com.planner.travel.domain.profile.entity.Profile;
import com.planner.travel.domain.profile.repository.ProfileRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.ApiDocumentUtil;
import com.planner.travel.global.jwt.token.TokenGenerator;
import com.planner.travel.global.jwt.token.TokenType;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.entity.Image;
import com.planner.travel.global.util.image.repository.ImageRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PlannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanBoxRepository planBoxRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PlannerRepository plannerRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private GroupMemberRepository groupMemberRepository;


    private Long userId1;
    private Long userId2;
    private Long plannerId1;
    private String validAccessToken1;
    private String validAccessToken2;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        planRepository.deleteAll();
        planBoxRepository.deleteAll();
        groupMemberRepository.deleteAll();
        plannerRepository.deleteAll();
        userRepository.deleteAll();
        profileRepository.deleteAll();
        imageRepository.deleteAll();

        Image image1 = Image.builder()
                .imageUrl("")
                .isDeleted(false)
                .isThumb(false)
                .category(Category.PROFILE)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(image1);

        Profile profile1 = Profile.builder()
                .image(image1)
                .build();

        profileRepository.save(profile1);

        User user1 = User.builder()
                .email("wldsmtldsm65@gmail.com")
                .password("123qwe!#QWE")
                .nickname("시은")
                .isWithdrawal(false)
                .birthday(LocalDate.parse("1996-11-20"))
                .signupDate(LocalDateTime.now())
                .userTag(1234L)
                .provider("basic")
                .profile(profile1)
                .build();

        Image image2 = Image.builder()
                .imageUrl("")
                .isDeleted(false)
                .isThumb(false)
                .category(Category.PROFILE)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(image2);

        Profile profile2 = Profile.builder()
                .image(image2)
                .build();

        profileRepository.save(profile2);

        User user2 = User.builder()
                .email("jieunnnn@gmail.com")
                .password("123qwe!#QWE")
                .nickname("지은")
                .isWithdrawal(false)
                .birthday(LocalDate.parse("1998-04-06"))
                .signupDate(LocalDateTime.now())
                .userTag(1234L)
                .provider("basic")
                .profile(profile2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        userId1 = user1.getId();
        userId2 = user2.getId();

        validAccessToken1 = "Bearer " + tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(userId1));
        validAccessToken2 = "Bearer " + tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(userId2));
    }

    private void createTestPlanners() {
        User user1 = userRepository.findById(userId1).get();

        Planner planner1 = Planner.builder()
                .isPrivate(true)
                .isDeleted(false)
                .title("테스트 플래너1")
                .user(user1)
                .startDate(String.valueOf(LocalDate.now()))
                .endDate(String.valueOf(LocalDate.now().plusDays(1)))
                .build();

        PlanBox planBox = PlanBox.builder()
                .planner(planner1)
                .planDate(LocalDate.now())
                .isDeleted(false)
                .build();

        Plan plan = Plan.builder()
                .planBox(planBox)
                .title("제목")
                .address("주소")
                .content("내용")
                .time(LocalTime.now())
                .isDeleted(false)
                .isPrivate(false)
                .build();

        Planner planner2 = Planner.builder()
                .isPrivate(false)
                .isDeleted(false)
                .title("테스트 플래너2")
                .user(user1)
                .startDate(String.valueOf(LocalDate.now()))
                .endDate(String.valueOf(LocalDate.now().plusDays(1)))
                .build();

        Planner planner3 = Planner.builder()
                .isPrivate(false)
                .isDeleted(false)
                .title("테스트 플래너3")
                .user(user1)
                .startDate(String.valueOf(LocalDate.now()))
                .endDate(String.valueOf(LocalDate.now().plusDays(1)))
                .build();

        GroupMember groupMember1 = GroupMember.builder()
                .planner(planner1)
                .isLeaved(false)
                .isHost(true)
                .user(user1)
                .build();

        GroupMember groupMember2 = GroupMember.builder()
                .planner(planner2)
                .isLeaved(false)
                .isHost(true)
                .user(user1)
                .build();

        GroupMember groupMember3 = GroupMember.builder()
                .planner(planner3)
                .isLeaved(false)
                .isHost(true)
                .user(user1)
                .build();

        plannerRepository.save(planner1);
        plannerRepository.save(planner2);
        plannerRepository.save(planner3);

        groupMemberRepository.save(groupMember1);
        groupMemberRepository.save(groupMember2);
        groupMemberRepository.save(groupMember3);

        planBoxRepository.save(planBox);
        planRepository.save(plan);

        plannerId1 = planner1.getId();
    }

    @Test
    @DisplayName("플래너 리스트 반환")
    public void getAllPlanner() throws Exception {
        createTestPlanners();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}/planners", userId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("getAllPlanners",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("[].plannerId").description("플래너 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].title").description("플래너 제목"),
                                PayloadDocumentation.fieldWithPath("[].startDate").description("여행 시작 날짜"),
                                PayloadDocumentation.fieldWithPath("[].endDate").description("여행 끝 날짜"),
                                PayloadDocumentation.fieldWithPath("[].isPrivate").description("플래너 공개 여부"),
                                PayloadDocumentation.fieldWithPath("[].profileImages").description("그룹 멤버 프로필 이미지")
                        )
                ));
    }

    @Test
    @DisplayName("플래너 리스트 반환 - 다른 유저")
    public void getAllPlannersOfOtherUser() throws Exception {
        createTestPlanners();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}/planners", userId1)
                        .header("Authorization", validAccessToken2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("getAllPlannersOfOtherUser",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("[].plannerId").description("플래너 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].title").description("플래너 제목"),
                                PayloadDocumentation.fieldWithPath("[].startDate").description("여행 시작 날짜"),
                                PayloadDocumentation.fieldWithPath("[].endDate").description("여행 끝 날짜"),
                                PayloadDocumentation.fieldWithPath("[].isPrivate").description("플래너 공개 여부"),
                                PayloadDocumentation.fieldWithPath("[].profileImages").description("그룹 멤버 프로필 이미지")
                        )
                ));
    }

    @Test
    @DisplayName("플래너 정보 반환")
    public void getPlanner() throws Exception {
        createTestPlanners();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}/planners/{plannerId}", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("getPlannerInfo",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("plannerId").description("플래너 ID"),
                                PayloadDocumentation.fieldWithPath("title").description("플래너 제목"),
                                PayloadDocumentation.fieldWithPath("startDate").description("플래너 시작 날짜"),
                                PayloadDocumentation.fieldWithPath("endDate").description("플래너 종료 날짜"),
                                PayloadDocumentation.fieldWithPath("isPrivate").description("플래너 비공개 여부"),
                                PayloadDocumentation.fieldWithPath("planBoxResponses").description("플랜 박스 응답 리스트")
                        ).andWithPrefix("planBoxResponses[].",
                                PayloadDocumentation.fieldWithPath("planBoxId").description("플랜 박스 ID"),
                                PayloadDocumentation.fieldWithPath("planDate").description("플랜 박스 날짜"),
                                PayloadDocumentation.fieldWithPath("planResponses").description("플랜 응답 리스트")
                        ).andWithPrefix("planBoxResponses[].planResponses[].",
                                PayloadDocumentation.fieldWithPath("planId").description("플랜 ID"),
                                PayloadDocumentation.fieldWithPath("isPrivate").description("플랜 비공개 여부"),
                                PayloadDocumentation.fieldWithPath("title").description("플랜 제목"),
                                PayloadDocumentation.fieldWithPath("time").description("플랜 시간"),
                                PayloadDocumentation.fieldWithPath("content").description("플랜 내용"),
                                PayloadDocumentation.fieldWithPath("address").description("플랜 주소")
                        )
                ));
    }

    @Test
    @DisplayName("플래너 생성")
    public void createPlanner() throws Exception {
        PlannerCreateRequest request = new PlannerCreateRequest(
                "테스트 플래너1",
                false
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/{userId}/planners", userId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("createPlanner",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("title").description("플래너 제목"),
                                PayloadDocumentation.fieldWithPath("isPrivate").description("플래너 공개 여부")
                        )
                ));
    }

    @Test
    @DisplayName("플래너 수정")
    public void updatePlanner() throws Exception {
        createTestPlanners();

        PlannerUpdateRequest request = new PlannerUpdateRequest(
                "테스트 플래너99",
                false
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/planners/{plannerId}", userId1, 1L)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("updatePlanner",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("플래너 삭제")
    public void deletePlanner() throws Exception {
        createTestPlanners();

        PlannerDeleteRequest request = new PlannerDeleteRequest(userId1);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/{userId}/planners/{plannerId}", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("deletePlanner",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("userId").description("유저 인덱스")
                        )
                ));
    }
}
