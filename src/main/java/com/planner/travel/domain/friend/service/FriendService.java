package com.planner.travel.domain.friend.service;

import com.planner.travel.domain.friend.dto.request.FriendRequest;
import com.planner.travel.domain.friend.dto.request.FriendRequestRequest;
import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.friend.entity.Status;
import com.planner.travel.domain.friend.query.FriendQueryService;
import com.planner.travel.domain.friend.repository.FriendRepository;
import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendQueryService friendQueryService;

    // 친구 요청
    @Transactional
    public void requestFriend(FriendRequestRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(EntityNotFoundException::new);

        User tempFriend = userRepository.findById(request.friendUserId())
                .orElseThrow(EntityNotFoundException::new);

        if (friendQueryService.validateFriend(request.userId()) == null) {
            Friend friend = Friend.builder()
                    .user(tempFriend)
                    .friend(user)
                    .status(Status.PENDING)
                    .build();

            friendRepository.save(friend);

        } else {
            Long friendId = friendQueryService.validateFriend(request.userId());
            Friend friend = friendRepository.findById(friendId)
                    .orElseThrow(EntityNotFoundException::new);

            if (friend.getStatus() == Status.UNFRIENDED) {
                friend = friend.withIsRequest();
                friendRepository.save(friend);
            }
        }
    }

    // 친구 승낙
    @Transactional
    public void acceptFriend(FriendRequest request) {
        Friend friend = friendRepository.findById(request.friendId())
                .orElseThrow(EntityNotFoundException::new);

        friend = friend.withIsAccepted();
        friendRepository.save(friend);
    }

    // 친구 삭제
    @Transactional
    public void deleteFriend(FriendRequest request) {
        Friend friend = friendRepository.findById(request.friendId())
                .orElseThrow(EntityNotFoundException::new);

        friend = friend.withIsDeleted();
        friendRepository.save(friend);
    }
}
