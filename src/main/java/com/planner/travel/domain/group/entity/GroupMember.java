package com.planner.travel.domain.group.entity;

import com.planner.travel.domain.planner.entity.Planner;
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
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isHost;

    private boolean isLeaved;


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "plannerId")
    private Planner planner;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    public void updateIsLeaved (boolean isLeaved) {
        this.isLeaved = isLeaved;
    }
}
