package com.planner.travel.domain.planner.entity;

import com.planner.travel.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    @Size(min = 2, max = 15)
    private String title;

    private String startDate;

    private String endDate;

    private boolean isDeleted;

    private boolean isPrivate;


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    public Planner withTitle(String title) {
        return Planner.builder()
                .id(this.id)
                .title(title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isDeleted(this.isDeleted)
                .isPrivate(this.isPrivate)
                .build();
    }

    public Planner withStartDate(String startDate) {
        return Planner.builder()
                .id(this.id)
                .title(this.title)
                .startDate(startDate)
                .endDate(this.endDate)
                .isDeleted(this.isDeleted)
                .isPrivate(this.isPrivate)
                .build();
    }

    public Planner withEndDate(String endDate) {
        return Planner.builder()
                .id(this.id)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(endDate)
                .isDeleted(this.isDeleted)
                .isPrivate(this.isPrivate)
                .build();
    }

    public Planner withIsPrivate(boolean isPrivate) {
        return Planner.builder()
                .id(this.id)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isDeleted(this.isDeleted)
                .isPrivate(isPrivate)
                .build();
    }

    public Planner withIsDeleted(boolean isDeleted) {
        return Planner.builder()
                .id(this.id)
                .title(this.title)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .isDeleted(isDeleted)
                .isPrivate(this.isPrivate)
                .build();
    }
}
