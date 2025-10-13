package com.chess.chess.game.model;

import com.chess.chess.model.Color;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveResult {
    private boolean success;
    private String message;
    private String newFen;
    private boolean check;
    private boolean checkmate;
    private boolean stalemate;
    private Color winner;
    private String capturedPiece;
    private String notation;

    public static MoveResult success(String msg, String newFen) {
        return MoveResult.builder()
                .success(true)
                .message(msg)
                .newFen(newFen)
                .build();
    }

    public static MoveResult error(String msg) {
        return MoveResult.builder()
                .success(false)
                .message(msg)
                .build();
    }

    // Fluent helpers for chaining
    public MoveResult withCheckmate(boolean value) { this.checkmate = value; return this; }
    public MoveResult withStalemate(boolean value) { this.stalemate = value; return this; }
    public MoveResult withWinner(Color winner) { this.winner = winner; return this; }
    public MoveResult withCapturedPiece(String piece) { this.capturedPiece = piece; return this; }
}
