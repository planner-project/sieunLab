package com.planner.travel.domain.friend.service;

import com.planner.travel.domain.friend.dto.request.FriendApproveRequest;
import com.planner.travel.domain.friend.dto.request.FriendDeleteRequest;
import com.planner.travel.domain.friend.dto.request.FriendRequestRequest;
import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.friend.repository.FriendRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 친구 요청
    @Transactional
    public void requestFriend(FriendRequestRequest request) {
        User user = userRepository.findById(request.friendUserId())
                .orElseThrow(EntityNotFoundException::new);

        Friend friend = Friend.builder()
                .user(user)
                .friendUserId(request.userId())
                .isRequested(false)
                .isAccepted(false)
                .requestedAt(LocalDateTime.now())
                .build();

        friendRepository.save(friend);
    }

    // 친구 승낙
    @Transactional
    public void acceptFriend(FriendApproveRequest request) {
        User user = userRepository.findById(request.friendUserId())
                .orElseThrow(EntityNotFoundException::new);

        Friend friend = Friend.builder()
                .user(user)
                .friendUserId(request.userId())
                .isRequested(true)
                .isAccepted(true)
                .build();

        friendRepository.save(friend);

        Friend existFriend = friendRepository.findById(request.friendId())
                .orElseThrow(EntityNotFoundException::new);

        existFriend = existFriend.withIsAccepted();

        friendRepository.save(existFriend);
    }

    // 친구 삭제
    @Transactional
    public void deleteFriend(FriendDeleteRequest request) {
        Friend friend1 = friendRepository.findById(request.friendId())
                .orElseThrow(EntityNotFoundException::new);

        friend1 = friend1.withIsDeleted();

        Friend friend2 = friendRepository.findById(request.friendFriendId())
                .orElseThrow(EntityNotFoundException::new);

        friend2 = friend2.withIsDeleted();

        friendRepository.save(friend1);
        friendRepository.save(friend2);
    }
}
