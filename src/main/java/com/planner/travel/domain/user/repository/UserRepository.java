package com.planner.travel.domain.user.repository;

import com.planner.travel.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<List<User>> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByUserTag(Long userTag);
}
