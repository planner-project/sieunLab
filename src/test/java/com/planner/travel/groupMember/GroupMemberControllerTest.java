package com.planner.travel.groupMember;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planner.travel.domain.group.dto.request.GroupMemberAddRequest;
import com.planner.travel.domain.group.dto.request.GroupMemberDeleteRequest;
import com.planner.travel.domain.group.dto.response.GroupMemberResponse;
import com.planner.travel.domain.group.entity.GroupMember;
import com.planner.travel.domain.group.repository.GroupMemberRepository;
import com.planner.travel.domain.group.service.GroupMemberService;
import com.planner.travel.domain.planner.entity.Planner;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class GroupMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupMemberService groupMemberService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PlannerRepository plannerRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    private Long userId1;
    private Long userId2;
    private Long userId3;

    private String validAccessToken1;
    private String validAccessToken2;

    private Long plannerId1;
    private Long plannerId2;
    private Long groupMemberId1;
    private Long groupMemberId2;

    @BeforeEach
    void setUp() {
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

        Image image2 = Image.builder()
                .imageUrl("")
                .isDeleted(false)
                .isThumb(false)
                .category(Category.PROFILE)
                .createdAt(LocalDateTime.now())
                .build();

        Image image3 = Image.builder()
                .imageUrl("")
                .isDeleted(false)
                .isThumb(false)
                .category(Category.PROFILE)
                .createdAt(LocalDateTime.now())
                .build();

        imageRepository.save(image1);
        imageRepository.save(image2);
        imageRepository.save(image3);

        Profile profile1 = Profile.builder()
                .image(image1)
                .build();

        Profile profile2 = Profile.builder()
                .image(image2)
                .build();

        Profile profile3 = Profile.builder()
                .image(image3)
                .build();

        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);

        User user1 = User.builder()
                .email("user1@example.com")
                .password("password")
                .nickname("User1")
                .isWithdrawal(false)
                .birthday(LocalDate.of(1990, 1, 1))
                .signupDate(LocalDateTime.now())
                .userTag(1001L)
                .provider("basic")
                .profile(profile1)
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .password("password")
                .nickname("User2")
                .isWithdrawal(false)
                .birthday(LocalDate.of(1992, 2, 2))
                .signupDate(LocalDateTime.now())
                .userTag(1002L)
                .provider("basic")
                .profile(profile2)
                .build();

        User user3 = User.builder()
                .email("user3@example.com")
                .password("password")
                .nickname("User3")
                .isWithdrawal(false)
                .birthday(LocalDate.of(1993, 3, 3))
                .signupDate(LocalDateTime.now())
                .userTag(1003L)
                .provider("basic")
                .profile(profile3)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        userId1 = user1.getId();
        userId2 = user2.getId();
        userId3 = user3.getId();

        validAccessToken1 = "Bearer " + tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(userId1));
        validAccessToken2 = "Bearer " + tokenGenerator.generateToken(TokenType.ACCESS, String.valueOf(userId2));

        Planner planner1 = Planner.builder()
                .isPrivate(false)
                .isDeleted(false)
                .title("Planner1")
                .startDate("2024-01-01")
                .endDate("2024-01-10")
                .build();

        Planner planner2 = Planner.builder()
                .isPrivate(true)
                .isDeleted(false)
                .title("Planner2")
                .startDate("2024-02-01")
                .endDate("2024-02-10")
                .build();

        plannerRepository.save(planner1);
        plannerRepository.save(planner2);

        GroupMember groupMember1 = GroupMember.builder()
                .planner(planner1)
                .user(user1)
                .isHost(true)
                .isLeaved(false)
                .build();

        GroupMember groupMember2 = GroupMember.builder()
                .planner(planner1)
                .user(user2)
                .isHost(false)
                .isLeaved(false)
                .build();

        GroupMember groupMember3 = GroupMember.builder()
                .planner(planner2)
                .user(user1)
                .isHost(true)
                .isLeaved(false)
                .build();

        GroupMember groupMember4 = GroupMember.builder()
                .planner(planner2)
                .user(user2)
                .isHost(false)
                .isLeaved(false)
                .build();

        groupMemberRepository.save(groupMember1);
        groupMemberRepository.save(groupMember2);
        groupMemberRepository.save(groupMember3);
        groupMemberRepository.save(groupMember4);

        plannerId1 = planner1.getId();
        plannerId2 = planner2.getId();
        groupMemberId1 = groupMember1.getId();
        groupMemberId2 = groupMember2.getId();
    }


    @Test
    @DisplayName("그룹 멤버 - 리스트 반환")
    public void getGroupMembers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}/planners/{plannerId}/group", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("getGroupMembers",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("[].groupMemberId").description("그룹 멤버 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].userId").description("유저 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].nickname").description("유저 닉네임"),
                                PayloadDocumentation.fieldWithPath("[].userTag").description("유저 태그"),
                                PayloadDocumentation.fieldWithPath("[].profileImageUrl").description("유저 프로필 이미지 URL"),
                                PayloadDocumentation.fieldWithPath("[].isHost").description("호스트 여부")
                        )
                ));
    }

    @Test
    @DisplayName("그룹 멤버 - 그룹 멤버 여부 확인")
    public void isGroupMember() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/{userId}/planners/{plannerId}/group/check", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("isGroupMember",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseBody()
                ));
    }

    @Test
    @DisplayName("그룹 멤버 - 추가")
    public void addGroupMember() throws Exception {
        GroupMemberAddRequest request = new GroupMemberAddRequest(userId3);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users/{userId}/planners/{plannerId}/group", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("addGroupMember",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("userId").description("유저 인덱스")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("[].groupMemberId").description("그룹 멤버 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].userId").description("유저 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].nickname").description("유저 닉네임"),
                                PayloadDocumentation.fieldWithPath("[].userTag").description("유저 태그"),
                                PayloadDocumentation.fieldWithPath("[].profileImageUrl").description("유저 프로필 이미지 URL"),
                                PayloadDocumentation.fieldWithPath("[].isHost").description("호스트 여부")
                        )
                ));
    }

    @Test
    @DisplayName("그룹 멤버 - 삭제")
    public void deleteGroupMember() throws Exception {
        GroupMemberDeleteRequest request = new GroupMemberDeleteRequest(groupMemberId2);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/api/v1/users/{userId}/planners/{plannerId}/group", userId1, plannerId1)
                        .header("Authorization", validAccessToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentation.document("deleteGroupMember",
                        ApiDocumentUtil.getDocumentRequest(),
                        ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("groupMemberId").description("그룹 멤버 인덱스")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("[].groupMemberId").description("그룹 멤버 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].userId").description("유저 인덱스"),
                                PayloadDocumentation.fieldWithPath("[].nickname").description("유저 닉네임"),
                                PayloadDocumentation.fieldWithPath("[].userTag").description("유저 태그"),
                                PayloadDocumentation.fieldWithPath("[].profileImageUrl").description("유저 프로필 이미지 URL"),
                                PayloadDocumentation.fieldWithPath("[].isHost").description("호스트 여부")
                        )
                ));
    }
}
