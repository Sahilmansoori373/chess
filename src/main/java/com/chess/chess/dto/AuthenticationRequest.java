package com.chess.chess.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String password;
}
