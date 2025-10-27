package com.chess.chess.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chess.chess.model.User;
import com.chess.chess.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService; // assume you have a UserService with findByUsername()
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthenticated"));
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullname", user.getFullname(),
                "score", user.getScore()
        );

        return ResponseEntity.ok(Map.of("status", "ok", "message", "Current user", "data", data));
    }
}
