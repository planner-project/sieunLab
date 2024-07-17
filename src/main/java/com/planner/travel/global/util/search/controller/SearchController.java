package com.planner.travel.global.util.search.controller;

import com.planner.travel.domain.planner.dto.response.PlannerListResponse;
import com.planner.travel.global.util.search.dto.UserSearchResponse;
import com.planner.travel.global.util.search.query.PlannerSearchService;
import com.planner.travel.global.util.search.query.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final UserSearchService userSearchService;
    private final PlannerSearchService plannerSearchService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSearchResponse>> findUser(@RequestParam("email") String email) {
        List<UserSearchResponse> userSearchResponse = userSearchService.findUserByEmail(email);

        return ResponseEntity.ok(userSearchResponse);
    }

    @GetMapping(value = "/planners")
    public Page<PlannerListResponse> findPlanners(@RequestParam("query") String input, Pageable pageable) {
        return plannerSearchService.searchUnPrivatePlanners(input, pageable);
    }

    @GetMapping(value = "/planners/{userId}")
    public Page<PlannerListResponse> findMyPlanners(@PathVariable("userId") Long userId, @RequestParam("query") String input, Pageable pageable) {
        return plannerSearchService.searchMyPlanners(userId, input, pageable);
    }
}
