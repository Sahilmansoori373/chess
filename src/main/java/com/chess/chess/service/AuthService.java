package com.chess.chess.service;

import java.util.Set;
import com.chess.chess.config.JwtUtil;
import com.chess.chess.dto.AuthResponse;
import com.chess.chess.dto.RegisterRequest;
import com.chess.chess.model.User;
import com.chess.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Register user and auto-login (return token)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .build();

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getFullName());
    }

    // Login user and return token + details
    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getFullName());
    }
}
