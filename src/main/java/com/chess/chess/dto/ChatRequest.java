package com.chess.chess.dto;

import jakarta.validation.constraints.*;

public record ChatRequest(
        @NotNull Long matchId,
        @NotBlank String content
) {}
