package com.planner.travel.global.util.search.controller;

import com.planner.travel.global.util.search.dto.UserSearchResponse;
import com.planner.travel.global.util.search.query.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class SearchController {
    private final UserSearchService userSearchService;

    @GetMapping("")
    public ResponseEntity<List<UserSearchResponse>> findUser(@RequestParam("email") String email) {
        List<UserSearchResponse> userSearchResponse = userSearchService.findUserByEmail(email);

        return ResponseEntity.ok(userSearchResponse);
    }
}
