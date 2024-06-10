package com.planner.travel.domain.planner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isPrivate;

    private String title;

    private LocalTime time;

    private String content;

    private String address;

    private boolean isDeleted;


    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "plannerBoxId")
    private PlanBox planBox;


    public void updateIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public void updateTitle(String title) {this.title = title;}
    public void updateTime(LocalTime time) {this.time = time;}
    public void updateContent(String content) {this.content = content;}
    public void updateAddress(String address) {this.address = address;}
    public void deleted(boolean isDeleted) {this.isDeleted = isDeleted;}


}


