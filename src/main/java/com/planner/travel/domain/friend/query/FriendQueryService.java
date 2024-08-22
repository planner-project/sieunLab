package com.planner.travel.domain.friend.query;

import com.planner.travel.domain.friend.dto.response.FriendResponse;
import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.friend.entity.QFriend;
import com.planner.travel.domain.friend.entity.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendQueryService {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public FriendQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // 승낙 기다리는 친구 리스트
    public List<FriendResponse> waitingAcceptList(Long userId) {
        QFriend qFriend = QFriend.friend1;

        List<Friend> friends = queryFactory
                .selectFrom(qFriend)
                .where(qFriend.user.id.eq(userId)
                        .and(qFriend.status.eq(Status.PENDING)))
                .orderBy(qFriend.id.desc())
                .fetch();

        List<FriendResponse> friendResponses = friends.stream()
                .map(friend ->  new FriendResponse(
                            friend.getId(),
                            friend.getFriend().getId(),
                            friend.getFriend().getNickname(),
                            friend.getFriend().getUserTag(),
                            friend.getFriend().getProfile().getProfileImageUrl()
                )).toList();

        return friendResponses;
    }


    // 친구 리스트 출력
    public List<FriendResponse> friends(Long userId) {
        QFriend qFriend = QFriend.friend1;

        List<Friend> friends = queryFactory
                .selectFrom(qFriend)
                .where(qFriend.user.id.eq(userId)
                        .or(qFriend.friend.id.eq(userId)))
                .where(qFriend.status.eq(Status.FRIENDED))
                .orderBy(qFriend.id.desc())
                .fetch();

        List<FriendResponse> friendResponses = friends.stream()
                .map(friend -> {
                    Long friendId = friend.getUser().getId().equals(userId) ? friend.getFriend().getId() : friend.getUser().getId();
                    String nickname = friend.getUser().getId().equals(userId) ? friend.getFriend().getNickname() : friend.getUser().getNickname();
                    Long userTag = friend.getUser().getId().equals(userId) ? friend.getFriend().getUserTag() : friend.getUser().getUserTag();
                    String profileImageUrl = friend.getUser().getId().equals(userId) ? friend.getFriend().getProfile().getProfileImageUrl() : friend.getUser().getProfile().getProfileImageUrl();

                    return new FriendResponse(
                            friend.getId(),
                            friendId,
                            nickname,
                            userTag,
                            profileImageUrl
                    );
                }).toList();

        return friendResponses;
    }

    public Long validateFriend(Long userId) {
        QFriend qFriend = QFriend.friend1;

        Long friendId = queryFactory
                .select(qFriend.id)
                .from(qFriend)
                .where(qFriend.user.id.eq(userId)
                        .and(qFriend.status.eq(Status.PENDING)))
                .fetchOne();


        return friendId;
    }
}
