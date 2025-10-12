package com.chess.chess.game.service;

import com.chess.chess.model.MoveResult;

public interface GameEngineService {
    MoveResult processMove(Long matchId, String srcAlgebraic, String dstAlgebraic, String username);
}
