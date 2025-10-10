package com.chess.chess.game;

import com.chess.chess.exception.InvalidMoveException;
import com.chess.chess.model.Color;
import com.chess.chess.game.util.FenConverter;
import com.chess.chess.game.rules.MoveValidator; // your package
import org.springframework.stereotype.Service;

@Service
public class GameEngineServiceImpl implements GameEngineService {

    // FenConverter converts fen <-> Board
    private final FenConverter fenConverter;

    public GameEngineServiceImpl(FenConverter fenConverter) {
        this.fenConverter = fenConverter;
    }

    @Override
    public MoveResult validateAndApplyMove(String fen, String from, String to, Color playerColor) {
        Board board = fenConverter.fromFEN(fen);

        Position src = FenConverter.toPosition(from);
        Position dst = FenConverter.toPosition(to);

        // Basic checks
        if (!src.isValid() || !dst.isValid()) {
            throw new InvalidMoveException("Invalid source or destination");
        }

        Piece moving = board.getPiece(src.row(), src.col());
        if (moving == null) throw new InvalidMoveException("No piece at source square");
        if (moving.getColor() != playerColor) throw new InvalidMoveException("Piece belongs to opponent");

        // Use your MoveValidator to check legality (including would-move-cause-check)
        if (!MoveValidator.isLegal(board, src, dst, playerColor)) {
            throw new InvalidMoveException("Move is illegal");
        }

        // Apply the move on the board instance (this is in-memory)
        Piece captured = board.getPiece(dst.row(), dst.col());
        board.movePiece(src, dst);

        // handle promotions, en-passant, castling — implement in engine
        // TODO: implement promotion selection rule; for now assume promotion to queen if eligible
        // Example:
        // if (moving.getType() == PieceType.PAWN && dst.isPromotionRank()) {
        //     board.setPiece(dst.row(), dst.col(), new Piece(PieceType.QUEEN, playerColor));
        // }

        // Recompute check/checkmate/stalemate using MoveValidator / Check detection
        boolean check = MoveValidator.isInCheck(board, playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        boolean checkmate = MoveValidator.isCheckmate(board, playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        boolean stalemate = MoveValidator.isStalemate(board, playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);

        Color winner = null;
        if (checkmate) winner = playerColor;

        String newFen = fenConverter.toFEN(board);
        String notation = MoveNotationUtil.generateNotation(moving, src, dst, captured); // implement notation util

        return new MoveResult(newFen, notation, check, checkmate, stalemate, captured != null ? captured.getType().name() : null, winner);
    }
}
