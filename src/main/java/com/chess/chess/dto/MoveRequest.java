package com.chess.chess.dto;

import jakarta.validation.constraints.*;

public record MoveRequest(
        @NotNull Long matchId,
        @NotBlank String from,   // "e2"
        @NotBlank String to     // "e4"
) {}
