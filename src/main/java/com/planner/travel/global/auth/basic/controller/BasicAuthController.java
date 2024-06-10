package com.planner.travel.global.auth.basic.controller;

import com.planner.travel.global.auth.basic.dto.request.LoginRequest;
import com.planner.travel.global.auth.basic.dto.response.SignupRequest;
import com.planner.travel.global.auth.basic.service.LoginService;
import com.planner.travel.global.auth.basic.service.SignupService;
import com.planner.travel.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class BasicAuthController {
    private final SignupService signupService;
    private final LoginService loginService;
    private final CookieUtil cookieUtil;


    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        signupService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        loginService.login(loginRequest, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/logout")
    public void logout(HttpServletResponse response) {
        response.setHeader("Authorization", "");
        cookieUtil.deleteCookie("refreshToken", response);
    }
}
