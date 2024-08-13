package com.planner.travel.domain.friend.controller;

import com.planner.travel.domain.friend.dto.request.FriendRequest;
import com.planner.travel.domain.friend.dto.request.FriendRequestRequest;
import com.planner.travel.domain.friend.dto.response.FriendResponse;
import com.planner.travel.domain.friend.query.FriendQueryService;
import com.planner.travel.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class FriendController {
    private final FriendQueryService friendQueryService;
    private final FriendService friendService;

    @GetMapping(value = "/users/{userId}/friends")
    public ResponseEntity<List<FriendResponse>> getFriends(@PathVariable("userId") Long userId) {
        List<FriendResponse> responses = friendQueryService.friends(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/users/{userId}/friends/waiting")
    public ResponseEntity<List<FriendResponse>> getWaitingAcceptFriends(@PathVariable("userId") Long userId) {
        List<FriendResponse> responses = friendQueryService.waitingAcceptList(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping(value = "/friends/request")
    public ResponseEntity<?> requestFriend(@RequestBody FriendRequestRequest request) {
        friendService.requestFriend(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/users/{userId}/friends/accept")
    public ResponseEntity<?> acceptFriend(@PathVariable("userId") Long userId, @RequestBody FriendRequest request) {
        friendService.acceptFriend(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/users/{userId}/friends/delete")
    public ResponseEntity<?> deleteFriend(@PathVariable("userId") Long userId, @RequestBody FriendRequest request) {
        friendService.deleteFriend(request);
        return ResponseEntity.ok().build();
    }
}
