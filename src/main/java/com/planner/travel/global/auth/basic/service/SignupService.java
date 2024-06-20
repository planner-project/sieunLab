package com.planner.travel.global.auth.basic.service;

import com.planner.travel.domain.profile.entity.Profile;
import com.planner.travel.domain.profile.repository.ProfileRepository;
import com.planner.travel.domain.user.entity.Sex;
import com.planner.travel.global.auth.basic.dto.request.SignupRequest;
import com.planner.travel.domain.user.entity.Role;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.util.RandomNumberUtil;
import com.planner.travel.global.util.RedisUtil;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.entity.Image;
import com.planner.travel.global.util.image.repository.ImageRepository;
import com.planner.travel.global.util.mail.dto.MailAuthenticaionMessage;
import com.planner.travel.global.util.mail.dto.request.MailAuthenticationRequest;
import com.planner.travel.global.util.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignupService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    private final RandomNumberUtil randomNumberUtil;
    private final RedisUtil redisUtil;
    private final MailService mailService;

    @Transactional
    public void signup(SignupRequest request) {
        userRepository.findByEmailAndProvider(request.email(), "basic")
                .ifPresent(u -> {
                    throw new IllegalArgumentException();
                });

        validateTempCode(request.email(), request.TempCode());

        Image image = Image.builder()
                .category(Category.PROFILE)
                .imageUrl("")
                .createdAt(LocalDateTime.now())
                .isThumb(false)
                .isDeleted(false)
                .build();

        imageRepository.save(image);

        Profile profile = Profile.builder()
                .image(image)
                .build();

        profileRepository.save(profile);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .userTag(randomNumberUtil.setUserCode())
                .role(Role.USER)
                .sex(Sex.NONE)
                .signupDate(LocalDateTime.now())
                .birthday(request.birthday())
//                .phoneNumber(signupRequest.getPhoneNumber())
                .isWithdrawal(false)
                .profile(profile)
                .provider("basic")
                .build();

        userRepository.save(user);
    }

    public void validateTempCode(String email, String tempCode) {
        String tempCodeFromRedis = redisUtil.getData(email);

        if (tempCode.equals(tempCodeFromRedis)) {
            throw new IllegalArgumentException();
        }
    }
}
