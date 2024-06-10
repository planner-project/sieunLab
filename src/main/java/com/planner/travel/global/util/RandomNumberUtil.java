package com.planner.travel.global.util;

import com.planner.travel.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class RandomNumberUtil {
    private final UserRepository userRepository;

    public Long setUserCode() {
        Random random = new Random();
        long randomNumber = random.nextLong(9000) + 1000;

        while (userRepository.findByUserTag(randomNumber).isPresent()) {
            randomNumber = random.nextLong(9000) + 1000;
        }
        return randomNumber;
    }

    public Long setTempCode() {
        Random random = new Random();
        long randomNumber = random.nextLong(900000) + 100000;
        return randomNumber;
    }
}