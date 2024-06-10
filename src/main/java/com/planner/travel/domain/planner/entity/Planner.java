package com.planner.travel.domain.planner.entity;

import com.planner.travel.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Planner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String startDate;

    private String endDate;

    private boolean isDeleted;

    private boolean isPrivate;


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void updateIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
