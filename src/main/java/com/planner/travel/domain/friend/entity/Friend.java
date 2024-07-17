package com.planner.travel.domain.friend.entity;

import com.planner.travel.domain.friend.controller.FriendController;
import com.planner.travel.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long friendFriendId;

    private Long friendUserId;

    private boolean isRequested;

    private boolean isAccepted;

    private boolean isDeleted;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    public Friend withIsDeleted() {
        return Friend.builder()
                .user(this.user)
                .friendUserId(this.friendUserId)
                .isRequested(this.isRequested)
                .isAccepted(this.isAccepted)
                .isDeleted(true)
                .build();
    }

    public Friend withIsAccepted() {
        return Friend.builder()
                .user(this.user)
                .friendUserId(this.friendUserId)
                .isRequested(this.isRequested)
                .isAccepted(true)
                .isDeleted(this.isDeleted)
                .build();
    }
}
