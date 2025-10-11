package com.chess.chess.game.util;

import com.chess.chess.game.model.Board;
import com.chess.chess.game.model.Position;

public class FenConverter {

    public Board fromFEN(String fen) {
        // TODO: Proper FEN parsing logic
        Board board = new Board();
        // For now, just placeholder for debugging
        board.setFen(fen);
        return board;
    }

    public String toFEN(Board board) {
        // TODO: Proper board-to-FEN logic
        return board.getFen();
    }

    public static Position toPosition(String algebraic) {
        if (algebraic == null || algebraic.length() != 2)
            throw new IllegalArgumentException("Invalid position: " + algebraic);

        char file = algebraic.charAt(0);
        char rank = algebraic.charAt(1);

        int col = file - 'a';
        int row = 8 - Character.getNumericValue(rank);

        return new Position(row, col);
    }
}
