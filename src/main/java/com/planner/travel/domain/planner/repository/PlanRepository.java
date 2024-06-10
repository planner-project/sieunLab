package com.planner.travel.domain.planner.repository;

import com.planner.travel.domain.planner.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
