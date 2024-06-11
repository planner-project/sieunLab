package com.planner.travel.global.auth.basic.service;

import com.planner.travel.domain.profile.entity.Profile;
import com.planner.travel.domain.profile.repository.ProfileRepository;
import com.planner.travel.domain.user.entity.Sex;
import com.planner.travel.global.auth.basic.dto.request.AuthenticationRequest;
import com.planner.travel.global.auth.basic.dto.request.SignupRequest;
import com.planner.travel.domain.user.entity.Role;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.util.RandomNumberUtil;
import com.planner.travel.global.util.RedisUtil;
import com.planner.travel.global.util.image.entity.Category;
import com.planner.travel.global.util.image.entity.Image;
import com.planner.travel.global.util.image.repository.ImageRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Transactional
    public void signup(SignupRequest request) {
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

    public void validateTempCode(AuthenticationRequest request) {
        String tempCodeFromRedis = redisUtil.getData(request.email());

        if (!request.tempCode().equals(tempCodeFromRedis)) {
            throw new IllegalArgumentException();
        }
    }
}
