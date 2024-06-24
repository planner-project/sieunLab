package com.planner.travel.domain.planner.controller;

import com.planner.travel.domain.planner.dto.request.PlannerCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlannerUpdateRequest;
import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.domain.planner.dto.response.PlannerResponse;
import com.planner.travel.domain.planner.service.PlannerListService;
import com.planner.travel.domain.planner.service.PlannerService;
import com.planner.travel.global.jwt.token.SubjectExtractor;
import com.planner.travel.global.jwt.token.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class PlannerController {
    private final PlannerListService plannerListService;
    private final PlannerService plannerService;
    private final TokenExtractor tokenExtractor;
    private final SubjectExtractor subjectExtractor;


    @GetMapping(value = "/{userId}/planners")
    public Page<PlannerListResponse> getPlanners(@PathVariable("userId") Long userId, HttpServletRequest request, Pageable pageable) {
        String accessToken = tokenExtractor.getAccessTokenFromHeader(request);
        Long subject = subjectExtractor.getUserIdFromToken(accessToken);
        boolean isLoginUser = subject.equals(userId);

        Page<PlannerListResponse> planners = plannerListService.getAllPlanners(userId, isLoginUser, pageable);

        return planners;
    }

    @GetMapping(value = "/{userId}/planners/{plannerId}")
    public ResponseEntity<PlannerResponse> getPlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId) {
        PlannerResponse planner = plannerService.getPlanner(plannerId);
        return ResponseEntity.ok(planner);
    }

    @PostMapping(value = "/{userId}/planners")
    public ResponseEntity<?> createPlanner(@PathVariable("userId") Long userId, @RequestBody PlannerCreateRequest request) {
        plannerListService.create(request, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{userId}/planners/{plannerId}")
    public ResponseEntity<?> updatePlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId, @RequestBody PlannerUpdateRequest request) {
        plannerListService.update(request, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{userId}/planners/{plannerId}")
    public ResponseEntity<?> deletePlanner(@PathVariable("userId") Long userId, @PathVariable("plannerId") Long plannerId) {
        plannerListService.delete(userId, plannerId);
        return ResponseEntity.ok().build();
    }
}
