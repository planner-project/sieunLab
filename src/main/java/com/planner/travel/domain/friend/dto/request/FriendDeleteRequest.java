package com.planner.travel.domain.friend.dto.request;

public record FriendDeleteRequest (
        Long friendId,
        Long friendFriendId
){
}
