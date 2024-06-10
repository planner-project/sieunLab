package com.planner.travel.global.jwt.token;

import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticator {
    private final SubjectExtractor subjectExtractor;
    private final UserRepository userRepository;

    public void getAuthenticationUsingToken(String accessToken) {
        if (accessToken.contains("Bearer")) {
            accessToken = accessToken.substring(7);
        }

        Long userId = subjectExtractor.getUserIdFromToken(accessToken);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException();
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user, accessToken, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
