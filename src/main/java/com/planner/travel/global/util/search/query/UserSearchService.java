package com.planner.travel.global.util.search.query;

import com.planner.travel.domain.user.entity.QUser;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.global.util.search.dto.UserSearchResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSearchService {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserSearchService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<UserSearchResponse> findUserByEmail(String email) {
        QUser qUser = QUser.user;

        List<User> users = queryFactory
                .select(qUser)
                .from(qUser)
                .where(qUser.email.eq(email))
                .fetch();

        List<UserSearchResponse> userSearchResponses = users.stream()
                .map(user -> {
                    String imageUrl = (user.getProfile() != null && user.getProfile().getImage() != null)
                            ? user.getProfile().getImage().getImageUrl()
                            : null;
                    return new UserSearchResponse(
                            user.getId(),
                            user.getNickname(),
                            user.getUserTag(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());

        return userSearchResponses;
    }
}
