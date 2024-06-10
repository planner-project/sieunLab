package com.planner.travel.domain.user.component;

import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;

    public User find(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        return user;
    }
}
