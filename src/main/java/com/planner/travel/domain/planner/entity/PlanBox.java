package com.planner.travel.domain.planner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate planDate;

    private boolean isDeleted;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "plannerId")
    private Planner planner;


    public void updatePlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }
    public void deleted(boolean isDeleted) {this.isDeleted = isDeleted;}

}
