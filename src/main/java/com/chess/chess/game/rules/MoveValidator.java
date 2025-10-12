package com.chess.chess.game.rules;

import com.chess.chess.game.model.Board;
import com.chess.chess.game.model.Position;
import com.chess.chess.model.Piece;
import com.chess.chess.model.PieceType;
import com.chess.chess.model.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic move validation and check/checkmate/stalemate detection.
 *
 * NOTE: This implementation intentionally omits castling, en-passant
 * and full promotion choices. It provides a solid foundation and
 * ensures isValidMove/isInCheck/isCheckmate/isStalemate logic works.
 */
public class MoveValidator {

    // Public API used by services
    public static boolean isValidMove(Board board, Position src, Position dst, Color color) {
        // Basic sanity
        if (src == null || dst == null || !src.isValid() || !dst.isValid()) return false;

        Piece moving = board.getPiece(src.getRow(), src.getCol());
        if (moving == null) return false;
        if (moving.getColor() != color) return false;

        // Cannot capture own piece
        Piece target = board.getPiece(dst.getRow(), dst.getCol());
        if (target != null && target.getColor() == color) return false;

        // Generate pseudo-legal moves for this piece
        List<Position> moves = generateMovesForPiece(board, src);
        boolean destinationAllowed = moves.stream().anyMatch(p -> p.equals(dst));
        if (!destinationAllowed) return false;

        // Simulate move and ensure own king isn't left in check
        return !wouldMoveCauseCheck(board, src, dst, color);
    }

    public static boolean isInCheck(Board board, Color color) {
        Position kingPos = findKingPosition(board, color);
        if (kingPos == null) return false; // no king => treat as no check (or invalid board)
        Color opponent = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

        // If any opponent's pseudo-legal move hits the king, it's a check
        List<Position> opponentPieces = allPiecePositionsOfColor(board, opponent);
        for (Position pos : opponentPieces) {
            List<Position> moves = generateMovesForPiece(board, pos);
            for (Position m : moves) {
                if (m.equals(kingPos)) return true;
            }
        }
        return false;
    }

    public static boolean isCheckmate(Board board, Color color) {
        if (!isInCheck(board, color)) return false;

        // If no legal move prevents check, it's checkmate
        List<Position> pieces = allPiecePositionsOfColor(board, color);
        for (Position from : pieces) {
            List<Position> moves = generateMovesForPiece(board, from);
            for (Position to : moves) {
                if (!wouldMoveCauseCheck(board, from, to, color)) {
                    return false; // found escape
                }
            }
        }
        return true;
    }

    public static boolean isStalemate(Board board, Color color) {
        // Stalemate: not in check and no legal moves
        if (isInCheck(board, color)) return false;

        List<Position> pieces = allPiecePositionsOfColor(board, color);
        for (Position from : pieces) {
            List<Position> moves = generateMovesForPiece(board, from);
            for (Position to : moves) {
                if (!wouldMoveCauseCheck(board, from, to, color)) {
                    return false; // has legal move
                }
            }
        }
        return true;
    }

    // --- Helper methods ---

    // Return list of positions that belong to pieces of specified color
    private static List<Position> allPiecePositionsOfColor(Board board, Color color) {
        List<Position> res = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == color) {
                    res.add(new Position(r, c));
                }
            }
        }
        return res;
    }

    // Find king position; null if not found
    private static Position findKingPosition(Board board, Color color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    // Simulate the move on a deep-copied board and check if color's king is in check
    private static boolean wouldMoveCauseCheck(Board board, Position src, Position dst, Color color) {
        Board copy = deepCopyBoard(board);

        Piece moving = copy.getPiece(src.getRow(), src.getCol());
        if (moving == null) return true; // invalid move

        // perform move on copy
        copy.movePiece(src.getRow(), src.getCol(), dst.getRow(), dst.getCol());

        // If moving a king, update its row/col (board.movePiece already does)
        // Now check if king of 'color' is in check on the copy
        return isInCheck(copy, color);
    }

    // Deep copy board and pieces (so we can safely simulate)
    private static Board deepCopyBoard(Board board) {
        Board copy = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null) {
                    // create new Piece instance duplicating fields
                    Piece pCopy = Piece.builder()
                            .color(p.getColor())
                            .type(p.getType())
                            .row(p.getRow())
                            .col(p.getCol())
                            .build();
                    copy.setPiece(r, c, pCopy);
                } else {
                    copy.setPiece(r, c, null);
                }
            }
        }
        return copy;
    }

    /**
     * Generate pseudo-legal moves for piece at `from`.
     * Pseudo-legal: obeys piece movement and captures, but DOES NOT check for leaving own king in check.
     */
    private static List<Position> generateMovesForPiece(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        Piece p = board.getPiece(from.getRow(), from.getCol());
        if (p == null) return moves;

        PieceType type = p.getType();
        Color color = p.getColor();

        switch (type) {
            case PAWN -> generatePawnMoves(board, from, color, moves);
            case KNIGHT -> generateKnightMoves(board, from, color, moves);
            case BISHOP -> generateSlidingMoves(board, from, color, moves, new int[][]{{-1,-1},{-1,1},{1,-1},{1,1}});
            case ROOK -> generateSlidingMoves(board, from, color, moves, new int[][]{{-1,0},{1,0},{0,-1},{0,1}});
            case QUEEN -> {
                generateSlidingMoves(board, from, color, moves, new int[][]{{-1,-1},{-1,1},{1,-1},{1,1}});
                generateSlidingMoves(board, from, color, moves, new int[][]{{-1,0},{1,0},{0,-1},{0,1}});
            }
            case KING -> generateKingMoves(board, from, color, moves);
        }

        return moves;
    }

    // Pawn moves (basic): forward, double from start, captures diagonally. No en-passant or promotion choices here.
    private static void generatePawnMoves(Board board, Position from, Color color, List<Position> out) {
        int dir = (color == Color.WHITE) ? -1 : 1; // white moves up (decreasing row)
        int r = from.getRow();
        int c = from.getCol();

        // forward one
        int r1 = r + dir;
        if (r1 >= 0 && r1 < 8 && board.getPiece(r1, c) == null) {
            out.add(new Position(r1, c));

            // double move from starting rank (white row 6, black row 1)
            int startRow = (color == Color.WHITE) ? 6 : 1;
            int r2 = r + 2*dir;
            if (r == startRow && r2 >= 0 && r2 < 8 && board.getPiece(r2, c) == null) {
                out.add(new Position(r2, c));
            }
        }

        // captures
        int[] dc = {-1, 1};
        for (int dcol : dc) {
            int cc = c + dcol;
            int rr = r + dir;
            if (rr >= 0 && rr < 8 && cc >= 0 && cc < 8) {
                Piece target = board.getPiece(rr, cc);
                if (target != null && target.getColor() != color) {
                    out.add(new Position(rr, cc));
                }
            }
        }
    }

    private static void generateKnightMoves(Board board, Position from, Color color, List<Position> out) {
        int[][] deltas = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        int r = from.getRow(), c = from.getCol();
        for (int[] d : deltas) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nr >= 8 || nc < 0 || nc >= 8) continue;
            Piece t = board.getPiece(nr, nc);
            if (t == null || t.getColor() != color) out.add(new Position(nr, nc));
        }
    }

    private static void generateKingMoves(Board board, Position from, Color color, List<Position> out) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = from.getRow() + dr, nc = from.getCol() + dc;
                if (nr < 0 || nr >= 8 || nc < 0 || nc >= 8) continue;
                Piece t = board.getPiece(nr, nc);
                if (t == null || t.getColor() != color) out.add(new Position(nr, nc));
            }
        }
        // Note: castling omitted here (needs rook moved flags and squares not under attack)
    }

    // Generic sliding moves for bishop/rook/queen
    private static void generateSlidingMoves(Board board, Position from, Color color, List<Position> out, int[][] directions) {
        for (int[] d : directions) {
            int dr = d[0], dc = d[1];
            int nr = from.getRow() + dr, nc = from.getCol() + dc;
            while (nr >= 0 && nr < 8 && nc >= 0 && nc < 8) {
                Piece t = board.getPiece(nr, nc);
                if (t == null) {
                    out.add(new Position(nr, nc));
                } else {
                    if (t.getColor() != color) out.add(new Position(nr, nc));
                    break; // blocked
                }
                nr += dr; nc += dc;
            }
        }
    }
}
