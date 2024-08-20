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
        long randomNumber = generateRandomNumberInRange(1000, 9999);

        while (userRepository.findByUserTag(randomNumber).isPresent()) {
            randomNumber = generateRandomNumberInRange(1000, 9999);
        }
        return randomNumber;
    }

    public Long setTempCode() {
        return generateRandomNumberInRange(100000, 999999);
    }

    private long generateRandomNumberInRange(long min, long max) {
        Random random = new Random();
        return min + (long) (random.nextDouble() * (max - min + 1));
    }
}
