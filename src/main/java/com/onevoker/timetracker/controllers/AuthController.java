package com.onevoker.timetracker.controllers;

import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.dto.auth.AuthResponse;
import com.onevoker.timetracker.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Validated @RequestBody AuthRequest authRequest) {
        return authService.register(authRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Validated @RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }
}
