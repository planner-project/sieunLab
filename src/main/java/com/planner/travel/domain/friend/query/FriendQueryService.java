package com.planner.travel.domain.friend.query;

import com.planner.travel.domain.friend.dto.response.FriendResponse;
import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.friend.entity.QFriend;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendQueryService {
    private final JPAQueryFactory queryFactory;
    private UserRepository userRepository;

    @Autowired
    public FriendQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // 승낙 기다리는 친구 리스트
    public List<FriendResponse> waitingAcceptList(Long userId) {
        QFriend qFriend = QFriend.friend;

        List<Friend> friends = queryFactory
                .selectFrom(qFriend)
                .where(qFriend.user.id.eq(userId)
                        .and(qFriend.isRequested.isFalse())
                        .and(qFriend.isAccepted.isFalse()))
                .orderBy(qFriend.requestedAt.desc())
                .fetch();

        return getFriendResponses(friends);
    }


    // 친구 리스트 출력
    public List<FriendResponse> friends(Long userId) {
        QFriend qFriend = QFriend.friend;

        List<Friend> friends = queryFactory
                .selectFrom(qFriend)
                .where(qFriend.user.id.eq(userId)
                        .and(qFriend.isRequested.isFalse())
                        .and(qFriend.isAccepted.isTrue()))
                .orderBy(qFriend.requestedAt.desc())
                .fetch();

        return getFriendResponses(friends);
    }


    private List<FriendResponse> getFriendResponses(List<Friend> friends) {
        List<FriendResponse> friendResponses = friends.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendUserId())
                            .orElseThrow(EntityNotFoundException::new);

                    return new FriendResponse(
                            friend.getFriendFriendId(),
                            friend.getFriendUserId(),
                            user.getNickname(),
                            user.getUserTag(),
                            user.getProfile().getProfileImageUrl()
                    );
                })
                .toList();

        return friendResponses;
    }
}
