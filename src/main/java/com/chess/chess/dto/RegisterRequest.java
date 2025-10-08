package com.chess.chess.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    private String fullname;
    private String username;
    private String password;
}
