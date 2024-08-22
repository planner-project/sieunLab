package com.planner.travel.domain.friend.entity;

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

    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendId")
    private User friend;


    public Friend withIsDeleted() {
        return Friend.builder()
                .id(this.id)
                .user(this.user)
                .friend(this.friend)
                .status(Status.UNFRIENDED)
                .build();
    }

    public Friend withIsRequest() {
        return Friend.builder()
                .id(this.id)
                .user(this.user)
                .friend(this.friend)
                .status(Status.PENDING)
                .build();
    }

    public Friend withIsAccepted() {
        return Friend.builder()
                .id(this.id)
                .user(this.user)
                .friend(this.friend)
                .status(Status.FRIENDED)
                .build();
    }

    public User getFriendOf(Long userId) {
        if (this.user.getId().equals(userId)) return this.friend;
        else return this.user;
    }
}
