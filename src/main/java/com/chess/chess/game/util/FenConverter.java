package com.chess.chess.game.util;

import com.chess.chess.game.model.Board;
import com.chess.chess.game.model.Position;
import com.chess.chess.model.Color;
import com.chess.chess.model.Piece;
import com.chess.chess.model.PieceType;

public class FenConverter {

    /**
     * Parse a FEN string into a Board object.
     */
    public static Board fromFEN(String fen) {
        if (fen == null || fen.isBlank()) {
            throw new IllegalArgumentException("FEN cannot be null or empty");
        }

        String[] parts = fen.trim().split(" ");
        String boardPart = parts[0];

        Board board = new Board();
        Piece[][] grid = new Piece[8][8];

        String[] rows = boardPart.split("/");
        if (rows.length != 8) {
            throw new IllegalArgumentException("Invalid FEN: must have 8 ranks");
        }

        for (int row = 0; row < 8; row++) {
            String rank = rows[row];
            int col = 0;

            for (char ch : rank.toCharArray()) {
                if (Character.isDigit(ch)) {
                    col += Character.getNumericValue(ch);
                } else {
                    Color color = Character.isUpperCase(ch) ? Color.WHITE : Color.BLACK;
                    PieceType type = switch (Character.toLowerCase(ch)) {
                        case 'p' -> PieceType.PAWN;
                        case 'r' -> PieceType.ROOK;
                        case 'n' -> PieceType.KNIGHT;
                        case 'b' -> PieceType.BISHOP;
                        case 'q' -> PieceType.QUEEN;
                        case 'k' -> PieceType.KING;
                        default -> throw new IllegalArgumentException("Invalid FEN piece: " + ch);
                    };

                    grid[row][col] = Piece.builder()
                            .color(color)
                            .type(type)
                            .row(row)
                            .col(col)
                            .build();
                    col++;
                }
            }

            if (col != 8) {
                throw new IllegalArgumentException("Invalid FEN rank: " + rank);
            }
        }

        board.setGrid(grid);
        return board;
    }

    /**
     * Convert a Board object into its FEN representation (without turn info).
     */
    public static String toFEN(Board board) {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    sb.append(piece.getSymbol());
                }
            }
            if (empty > 0) sb.append(empty);
            if (row != 7) sb.append('/');
        }

        return sb.toString();
    }

    /**
     * Convert algebraic notation (like "e4") to a Position object.
     */
    public static Position toPosition(String algebraic) {
        if (algebraic == null || algebraic.length() != 2)
            throw new IllegalArgumentException("Invalid position: " + algebraic);

        char file = algebraic.charAt(0);
        char rank = algebraic.charAt(1);

        int col = file - 'a';
        int row = 8 - Character.getNumericValue(rank);

        if (row < 0 || row >= 8 || col < 0 || col >= 8)
            throw new IllegalArgumentException("Invalid coordinates: " + algebraic);

        return new Position(row, col);
    }

    /**
     * Convert Position to algebraic notation (like "(6,4)" -> "e2").
     */
    public static String fromPosition(Position pos) {
        if (pos == null || !pos.isValid())
            throw new IllegalArgumentException("Invalid position");

        char file = (char) ('a' + pos.getCol());
        char rank = (char) ('8' - pos.getRow());
        return "" + file + rank;
    }
}
