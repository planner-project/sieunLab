package com.planner.travel.domain.message.service;

import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.friend.repository.FriendRepository;
import com.planner.travel.domain.message.dto.request.MessageSendRequest;
import com.planner.travel.domain.message.entity.Message;
import com.planner.travel.domain.message.repository.MessageRepository;
import com.planner.travel.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final FriendRepository friendRepository;

    public void sendMessage(MessageSendRequest request) {
        Friend friend = friendRepository.findById(request.friendId())
                .orElseThrow(EntityNotFoundException::new);

        User user1 = friend.getUser();
        User user2 = friend.getFriend();

        Message message;

        if (request.sendUserId().equals(user1.getId())) {
            message = Message.builder()
                    .friend(friend)
                    .sendUser(user1)
                    .receivedUser(user2)
                    .content(request.content())
                    .isDeleted(false)
                    .sendAt(LocalDateTime.now())
                    .build();

            messageRepository.save(message);

        } else {
            message = Message.builder()
                    .friend(friend)
                    .sendUser(user2)
                    .receivedUser(user1)
                    .content(request.content())
                    .isDeleted(false)
                    .sendAt(LocalDateTime.now())
                    .build();

            messageRepository.save(message);
        }
    }
}
