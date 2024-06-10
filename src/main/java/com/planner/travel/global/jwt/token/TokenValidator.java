package com.planner.travel.global.jwt.token;

import com.planner.travel.domain.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenValidator {
    private final UserRepository userRepository;

    @Autowired
    private Key key;

    public void validateAccessToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

        } catch (ExpiredJwtException e) {
            throw e;
        }
    }
}
