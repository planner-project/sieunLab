package com.planner.travel.domain.profile.service;

import com.planner.travel.domain.profile.dto.request.UserInfoUpdateRequest;
import com.planner.travel.domain.user.component.UserFinder;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoUpdateService {
    private final UserFinder userFinder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void update(Long userId, UserInfoUpdateRequest request) {
        User user = userFinder.find(userId);

        if (isValid(request.password())) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }

        if (isValid(request.nickname())) {
            user.updateNickname(request.nickname());
        }

        if (request.birthday() != null) {
            user.updateBirthday(request.birthday());
        }

//        if (isValid(request.phoneNumber())) {
//            user.updatePhoneNumber(request.phoneNumber());
//        }

        if (isValid(request.sex().toString())) {
            user.updateSex(request.sex());
        }

        userRepository.save(user);
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawal(Long userId) {
        User user = userFinder.find(userId);
        user.updateWithdrawal(true);

        userRepository.save(user);
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }

}
