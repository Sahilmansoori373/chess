package com.chess.chess.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank String fullname,
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6, max = 100) String password
) {}
