package com.chess.chess.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Piece {
    private Color color;
    private PieceType type;
    private int row;
    private int col;

    public String getSymbol() {
        // Uppercase for white, lowercase for black
        char symbol = switch (type) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
        return color == Color.WHITE ? String.valueOf(symbol) : String.valueOf(Character.toLowerCase(symbol));
    }
}
