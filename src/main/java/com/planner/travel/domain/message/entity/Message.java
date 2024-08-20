package com.planner.travel.domain.message.entity;

import com.planner.travel.domain.friend.entity.Friend;
import com.planner.travel.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sendUser")
    private User sendUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivedUser")
    private User receivedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friendId")
    private Friend friend;

    private String content;

    private boolean isDeleted;

    private LocalDateTime sendAt;


    public Message withDeleted() {
        return Message.builder()
                .id(this.id)
                .sendUser(this.sendUser)
                .receivedUser(this.receivedUser)
                .content(this.content)
                .isDeleted(true)
                .sendAt(this.sendAt)
                .build();
    }
}
