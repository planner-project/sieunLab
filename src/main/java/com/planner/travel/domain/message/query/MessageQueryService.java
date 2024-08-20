package com.planner.travel.domain.message.query;

import com.planner.travel.domain.friend.entity.QFriend;
import com.planner.travel.domain.message.dto.request.MessagesRequest;
import com.planner.travel.domain.message.dto.response.MessageListResponse;
import com.planner.travel.domain.message.dto.response.MessageResponse;
import com.planner.travel.domain.message.entity.QMessage;
import com.planner.travel.domain.user.entity.QUser;
import com.planner.travel.global.util.PaginationUtil;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageQueryService {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public MessageQueryService(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<MessageListResponse> getMessageList(Long userId, Pageable pageable) {
        QFriend friend = QFriend.friend1;
        QMessage message = QMessage.message;
        QMessage subMessage = new QMessage("subMessage");

        List<Tuple> results = queryFactory
                .select(friend,
                        message.content
                )
                .from(friend)
                .leftJoin(message).on(message.friend.id.eq(friend.id))
                .where(friend.user.id.eq(userId).or(friend.friend.id.eq(userId))
                        .and(message.sendAt.in(
                                JPAExpressions
                                        .select(subMessage.sendAt.max())
                                        .from(subMessage)
                                        .where(subMessage.friend.id.eq(friend.id))
                        )))
                .orderBy(message.sendAt.desc())
                .fetch();

        List<MessageListResponse> content = new ArrayList<>();
        for (Tuple tuple : results) {
            Long friendId;
            String nickname;
            String profileImageUrl;
            Long friendTag;

            if (tuple.get(friend).getFriend().getId().equals(userId)) {
                friendId = tuple.get(friend).getId();
                nickname = tuple.get(friend).getUser().getNickname();
                profileImageUrl = tuple.get(friend).getUser().getProfile().getProfileImageUrl();
                friendTag = tuple.get(friend).getUser().getUserTag();

            } else  {
                friendId = tuple.get(friend).getId();
                nickname = tuple.get(friend).getFriend().getNickname();
                profileImageUrl = tuple.get(friend).getFriend().getProfile().getProfileImageUrl();
                friendTag = tuple.get(friend).getFriend().getUserTag();
            }

            String recentMessage = tuple.get(message.content);
            MessageListResponse dto = new MessageListResponse(friendId, nickname, profileImageUrl, friendTag, recentMessage);
            content.add(dto);
        }

        return PaginationUtil.listToPage(content, pageable);
    }


    public Page<MessageResponse> getMessages(MessagesRequest request, Pageable pageable) {
        QMessage message = QMessage.message;
        QUser sendUser = new QUser("sendUser");
        QUser receivedUser = new QUser("receivedUser");

        List<Tuple> results = queryFactory
                .select(message.id,
                        sendUser.id,
                        sendUser.nickname,
                        sendUser.userTag,
                        sendUser.profile.profileImageUrl,
                        receivedUser.id,
                        receivedUser.nickname,
                        receivedUser.userTag,
                        receivedUser.profile.profileImageUrl,
                        message.content,
                        message.sendAt
                )
                .from(message)
                .leftJoin(message.sendUser, sendUser)
                .leftJoin(message.receivedUser, receivedUser)
                .where(message.friend.id.eq(request.friendId()))
                .orderBy(message.sendAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MessageResponse> content = new ArrayList<>();
        for (Tuple tuple : results) {
            boolean isSent = tuple.get(sendUser.id).equals(request.userId());
            MessageResponse dto = new MessageResponse(
                    tuple.get(message.id),
                    isSent,
                    tuple.get(sendUser.id),
                    tuple.get(sendUser.nickname),
                    tuple.get(sendUser.userTag),
                    tuple.get(sendUser.profile.profileImageUrl),
                    tuple.get(receivedUser.id),
                    tuple.get(receivedUser.nickname),
                    tuple.get(receivedUser.userTag),
                    tuple.get(receivedUser.profile.profileImageUrl),
                    tuple.get(message.content),
                    tuple.get(message.sendAt)
            );
            content.add(dto);
        }

        return PaginationUtil.listToPage(content, pageable);
    }
}