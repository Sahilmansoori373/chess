package com.chess.chess.game;

import com.chess.chess.exception.InvalidMoveException;
import com.chess.chess.game.model.MoveResult;
import com.chess.chess.model.Color;

public interface GameEngineService {
    /**
     * Validates and applies a move on the given FEN.
     *
     * @param fen current fen
     * @param from source square, e.g. "e2"
     * @param to   target square, e.g. "e4"
     * @param playerColor color of the moving player
     * @return result object containing new fen, move notation, whether check/checkmate/stalemate, captured piece etc.
     * @throws InvalidMoveException when move is illegal
     */
    MoveResult validateAndApplyMove(String fen, String from, String to, Color playerColor);
}
