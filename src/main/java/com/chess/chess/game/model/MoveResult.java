package com.chess.chess.game.model;

import com.chess.chess.model.Color;

public class MoveResult {
    private final String newFen;
    private final String notation;
    private final boolean check;
    private final boolean checkmate;
    private final boolean stalemate;
    private final String capturedPiece;
    private final Color winner; // null if none

    public MoveResult(String newFen, String notation, boolean check, boolean checkmate,
                      boolean stalemate, String capturedPiece, Color winner) {
        this.newFen = newFen;
        this.notation = notation;
        this.check = check;
        this.checkmate = checkmate;
        this.stalemate = stalemate;
        this.capturedPiece = capturedPiece;
        this.winner = winner;
    }

    // getters...
    public String getNewFen() { return newFen; }
    public String getNotation() { return notation; }
    public boolean isCheck() { return check; }
    public boolean isCheckmate() { return checkmate; }
    public boolean isStalemate() { return stalemate; }
    public String getCapturedPiece() { return capturedPiece; }
    public Color getWinner() { return winner; }
}
