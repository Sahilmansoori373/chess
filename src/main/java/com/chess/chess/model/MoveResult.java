package com.chess.chess.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveResult {
    private boolean success;
    private String message;
    private String fen;

    public static MoveResult success(String msg, String fen) {
        return MoveResult.builder()
                .success(true)
                .message(msg)
                .fen(fen)
                .build();
    }

    public static MoveResult error(String msg) {
        return MoveResult.builder()
                .success(false)
                .message(msg)
                .build();
    }
}
