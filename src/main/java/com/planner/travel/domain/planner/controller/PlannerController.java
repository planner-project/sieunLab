package com.planner.travel.domain.planner.controller;

import com.planner.travel.domain.planner.dto.request.PlannerCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlannerUpdateRequest;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.dto.response.PlannerResponse;
import com.planner.travel.domain.planner.query.PlannerQueryService;
import com.planner.travel.domain.planner.service.PlannerListService;
import com.planner.travel.domain.planner.service.PlannerService;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import com.planner.travel.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class PlannerController {
    private final PlannerQueryService plannerQueryService;
    private final PlannerListService plannerListService;
    private final PlannerService plannerService;

    @GetMapping(value = "/planners")
    public Page<PlannerListResponse> getAllPlanners(Pageable pageable) {
        return plannerQueryService.findUnPrivatePlanners(pageable);
    }

    @GetMapping(value = "/users/{userId}/planners")
    public Page<PlannerListResponse> getPlanners(
            @PathVariable("userId") Long userId,
            Pageable pageable,
            HttpServletRequest request
    ) {
        return plannerListService.getAllPlanners(userId, pageable, request);
    }

    @GetMapping(value = "/users/{userId}/planners/{plannerId}")
    public ResponseEntity<PlannerResponse> getPlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId, HttpServletRequest request) {
        PlannerResponse planner = plannerService.getPlanner(userId, plannerId, request);
        return ResponseEntity.ok(planner);
    }

    @PostMapping(value = "/users/{userId}/planners")
    public ResponseEntity<?> createPlanner(@PathVariable("userId") Long userId, @RequestBody PlannerCreateRequest request) {
        plannerListService.create(request, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/users/{userId}/planners/{plannerId}")
    public ResponseEntity<?> updatePlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId, @RequestBody PlannerUpdateRequest request) {
        plannerListService.update(request, plannerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/users/{userId}/planners/{plannerId}")
    public ResponseEntity<?> deletePlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId) {
        plannerListService.delete(userId, plannerId);
        return ResponseEntity.ok().build();
    }
}
