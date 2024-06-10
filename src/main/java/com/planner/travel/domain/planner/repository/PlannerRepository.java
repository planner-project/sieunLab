package com.planner.travel.domain.planner.repository;

import com.planner.travel.domain.planner.entity.Planner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlannerRepository extends JpaRepository<Planner, Long> {
}
