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

    // ✅ Register user and auto-login (return token)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }


        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword())) // store hashed password
                .roles(Set.of("ROLE_USER"))
                .build();

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getFullName());
    }

    public AuthResponse login(String username, String password) {
        System.out.println("➡️ Login attempt: " + username + " / " + password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("➡️ Stored password hash: " + user.getPassword());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("❌ Password mismatch!");
            throw new RuntimeException("Invalid password");
        }

        System.out.println("✅ Password matched!");
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getFullName());
    }

//    // ✅ Login user and return token + details
//    public AuthResponse login(String username, String password) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Compare raw password with encoded DB password
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        // Generate JWT token
//        String token = jwtUtil.generateToken(user.getUsername());
//
//        return new AuthResponse(token, user.getUsername(), user.getFullName());
//    }
}
