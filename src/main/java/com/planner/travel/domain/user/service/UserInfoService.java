package com.planner.travel.domain.user.service;

import com.planner.travel.domain.user.dto.response.UserInfoResponse;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserInfoService {
    private final UserRepository userRepository;

    public UserInfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfoResponse get(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new UserInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getUserTag(),
                user.getBirthday(),
                user.getEmail(),
                user.getProfile().getImage().getImageUrl(),
                isBirthdayToday(user.getBirthday()),
                user.getSex()
        );
    }

    private boolean isBirthdayToday(LocalDate birthday) {
        return birthday != null && birthday.getMonth() == LocalDate.now().getMonth() &&
                birthday.getDayOfMonth() == LocalDate.now().getDayOfMonth();
    }
}
