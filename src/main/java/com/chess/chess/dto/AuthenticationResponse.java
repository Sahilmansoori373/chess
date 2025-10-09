package com.chess.chess.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String message;
}
