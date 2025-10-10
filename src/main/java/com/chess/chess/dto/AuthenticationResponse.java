package com.chess.chess.dto;

public record AuthenticationResponse(
        String token,
        String message
) {}
