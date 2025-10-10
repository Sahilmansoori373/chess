package com.chess.chess.dto;

import jakarta.validation.constraints.*;

public record StartMatchRequest(
        @NotNull Long opponentId  // ID of opponent user
) {}
