package com.chess.chess.controller;

import com.chess.chess.dto.AuthResponse;
import com.chess.chess.dto.RegisterRequest;
import com.chess.chess.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }
}

