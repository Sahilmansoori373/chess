package com.chess.chess.game.service;

import com.chess.chess.game.model.Board;
import com.chess.chess.game.model.Position;
import com.chess.chess.game.rules.MoveValidator;
import com.chess.chess.game.util.FenConverter;
import com.chess.chess.game.model.MoveResult;
import com.chess.chess.model.*;
import com.chess.chess.repository.BoardEntityRepository;
import com.chess.chess.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameEngineServiceImpl implements GameEngineService {

    private final MatchRepository matchRepository;
    private final BoardEntityRepository boardEntityRepository;

    /**
     * Process a move request (like "e2" → "e4").
     */
    @Override
    @Transactional
    public MoveResult processMove(Long matchId, String srcAlgebraic, String dstAlgebraic, String username) {
        // 1️⃣ Fetch match
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        // 2️⃣ Load board entity (persistent FEN)
        BoardEntity boardEntity = boardEntityRepository.findByMatch(match)
                .orElseThrow(() -> new IllegalStateException("Board not found for match: " + matchId));

        // 3️⃣ Load board state in memory
        Board board = FenConverter.fromFEN(boardEntity.getFen());

        // 4️⃣ Identify player color
        Color playerColor;
        if (match.getWhitePlayer().getUsername().equals(username)) {
            playerColor = Color.WHITE;
        } else if (match.getBlackPlayer().getUsername().equals(username)) {
            playerColor = Color.BLACK;
        } else {
            return MoveResult.error("You are not a player in this match!");
        }

        // 5️⃣ Turn validation
        if (boardEntity.getTurnColor() != null && boardEntity.getTurnColor() != playerColor) {
            return MoveResult.error("It's not your turn!");
        }

        // 6️⃣ Convert algebraic notation (e.g., "e2" → [row, col])
        Position src = FenConverter.toPosition(srcAlgebraic);
        Position dst = FenConverter.toPosition(dstAlgebraic);

        // 7️⃣ Validate the piece and ownership
        Piece moving = board.getPiece(src.getRow(), src.getCol());
        if (moving == null) return MoveResult.error("No piece at " + srcAlgebraic);
        if (moving.getColor() != playerColor) return MoveResult.error("You cannot move opponent's piece");

        // 8️⃣ Validate legality
        if (!MoveValidator.isValidMove(board, src, dst, playerColor)) {
            return MoveResult.error("Illegal move for " + moving.getType());
        }

        // 9️⃣ Execute move
        Piece captured = board.getPiece(dst.getRow(), dst.getCol());
        board.movePiece(src.getRow(), src.getCol(), dst.getRow(), dst.getCol());

        // 🔟 Update FEN and turn color
        boardEntity.setFen(FenConverter.toFEN(board));
        boardEntity.setTurnColor(playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);

        // Append to move history
        String moveHistory = (boardEntity.getMoveHistory() == null ? "" : boardEntity.getMoveHistory() + " ")
                + srcAlgebraic + "-" + dstAlgebraic;
        boardEntity.setMoveHistory(moveHistory);

        // 11️⃣ Check for game-end conditions
        Color opponent = (playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);

        if (MoveValidator.isCheckmate(board, opponent)) {
            match.setWinner(match.getPlayerByColor(playerColor));
            match.setStatus(GameStatus.COMPLETED);
            matchRepository.save(match);
            boardEntityRepository.save(boardEntity);
            return MoveResult.success("Checkmate! " + playerColor + " wins.", boardEntity.getFen());
        }

        if (MoveValidator.isStalemate(board, opponent)) {
            match.setStatus(GameStatus.DRAW);
            matchRepository.save(match);
            boardEntityRepository.save(boardEntity);
            return MoveResult.success("Stalemate. The game is a draw.", boardEntity.getFen());
        }

        // 12️⃣ Save updates
        matchRepository.save(match);
        boardEntityRepository.save(boardEntity);

        // 13️⃣ Return success result
        String msg = (captured != null)
                ? "Captured " + captured.getType() + " on " + dstAlgebraic
                : "Moved " + moving.getType() + " from " + srcAlgebraic + " to " + dstAlgebraic;

        return MoveResult.success(msg, boardEntity.getFen());
    }
}
