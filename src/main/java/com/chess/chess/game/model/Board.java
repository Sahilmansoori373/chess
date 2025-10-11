package com.chess.chess.game.model;

import lombok.*;
import com.chess.chess.model.Piece;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private Piece[][] grid = new Piece[8][8];

    public Piece getPiece(int row, int col) {
        if (isOutOfBounds(row, col)) return null;
        return grid[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        if (!isOutOfBounds(row, col)) grid[row][col] = piece;
    }

    public void movePiece(int srcRow, int srcCol, int dstRow, int dstCol) {
        if (isOutOfBounds(srcRow, srcCol) || isOutOfBounds(dstRow, dstCol))
            throw new IllegalArgumentException("Invalid board coordinates");

        Piece moving = getPiece(srcRow, srcCol);
        grid[dstRow][dstCol] = moving;
        grid[srcRow][srcCol] = null;

        if (moving != null) {
            moving.setRow(dstRow);
            moving.setCol(dstCol);
        }
    }

    public boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= 8 || col < 0 || col >= 8;
    }
}
