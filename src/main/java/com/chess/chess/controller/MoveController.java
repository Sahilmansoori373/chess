package com.chess.chess.controller;

import com.chess.chess.dto.*;
import com.chess.chess.exception.*;
import com.chess.chess.game.model.MoveResult;
import com.chess.chess.game.service.GameEngineService;
import com.chess.chess.model.*;
import com.chess.chess.service.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/move")
public class MoveController {

    private final MatchService matchService;
    private final BoardStateService boardStateService;
    private final MoveService moveService;
    private final GameEngineService gameEngineService;
    private final UserService userService;
    private final UserScoreHistoryService scoreHistoryService;
    private final MessageService messageService;

    public MoveController(MatchService matchService, BoardStateService boardStateService,
                          MoveService moveService, GameEngineService gameEngineService,
                          UserService userService, UserScoreHistoryService scoreHistoryService,
                          MessageService messageService) {
        this.matchService = matchService;
        this.boardStateService = boardStateService;
        this.moveService = moveService;
        this.gameEngineService = gameEngineService;
        this.userService = userService;
        this.scoreHistoryService = scoreHistoryService;
        this.messageService = messageService;
    }

    @PostMapping("/play")
    @Transactional
    public ResponseEntity<ApiResponse> playMove(@Validated @RequestBody MoveRequest req, @RequestHeader("X-USER-ID") Long userId) {
        Match match = matchService.findById(req.matchId()).orElseThrow(() -> new NotFoundException("Match not found"));
        if (match.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ConflictException("Match is not in progress");
        }

        User player = userService.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        // Determine player color
        Color playerColor = (match.getWhitePlayer().getId().equals(player.getId())) ? Color.WHITE :
                (match.getBlackPlayer().getId().equals(player.getId())) ? Color.BLACK : null;
        if (playerColor == null) throw new ConflictException("Player is not a participant of the match");

        // Enforce turn: derive from move count
        int movesSoFar = match.getMoves().size();
        Color expected = (movesSoFar % 2 == 0) ? Color.WHITE : Color.BLACK;
        if (expected != playerColor) throw new ConflictException("Not player's turn");

        // Load board state
        BoardEntity boardState = boardStateService.findByMatch(match)
                .orElseThrow(() -> new NotFoundException("Board state not found"));

        // Validate and apply move via engine (throws InvalidMoveException on illegal)
        MoveResult result = gameEngineService.validateAndApplyMove(boardState.getFen(), req.from(), req.to(), playerColor);

        // Persist move entity
        Move moveEntity = Move.builder()
                .match(match)
                .player(player)
                .fromPosition(req.from())
                .toPosition(req.to())
                .piece(result.getNotation()) // optionally set piece, notation
                .capturedPiece(result.getCapturedPiece())
                .moveNumber(match.getMoves().size() + 1)
                .moveNotation(result.getNotation())
                .color(playerColor)
                .build();
        moveService.saveMove(moveEntity);

        // Update BoardState FEN
        boardState.setFen(result.getNewFen());
        boardStateService.save(boardState);

        // Update match move list (optional for sync; ensure relationships are maintained)
        match.getMoves().add(moveEntity);

        // If result ends game mark match and update scores
        if (result.isCheckmate() || result.isStalemate()) {
            match.setStatus(GameStatus.COMPLETED);
            if (result.getWinner() != null) {
                User winner = (result.getWinner() == Color.WHITE) ? match.getWhitePlayer() : match.getBlackPlayer();
                User loser = (winner == match.getWhitePlayer()) ? match.getBlackPlayer() : match.getWhitePlayer();

                int deltaWin = 30;
                int deltaLose = 20;

                // Update scores and history
                int beforeWinner = winner.getScore();
                int beforeLoser = loser.getScore();
                winner.setScore(winner.getScore() + deltaWin);
                loser.setScore(loser.getScore() - deltaLose);
                matchService.save(match);

                scoreHistoryService.save(UserScoreHistory.builder()
                        .user(winner)
                        .scoreBefore(beforeWinner)
                        .scoreAfter(winner.getScore())
                        .match(match)
                        .build());

                scoreHistoryService.save(UserScoreHistory.builder()
                        .user(loser)
                        .scoreBefore(beforeLoser)
                        .scoreAfter(loser.getScore())
                        .match(match)
                        .build());

                // Persist system message
                messageService.sendMessage(Message.builder()
                        .match(match).sender(null) // null for system
                        .content("Game ended. Winner: " + winner.getUsername())
                        .build());
            } else {
                match.setStatus(GameStatus.COMPLETED);
            }
            matchService.save(match);
        } else {
            // Save match to persist version change (important for optimistic locking)
            matchService.save(match);
        }

        // Return new fen and metadata
        Map<String, Object> resp = new HashMap<>();
        resp.put("fen", result.getNewFen());
        resp.put("notation", result.getNotation());
        resp.put("check", result.isCheck());
        resp.put("checkmate", result.isCheckmate());
        resp.put("stalemate", result.isStalemate());
        resp.put("captured", result.getCapturedPiece());

        return ResponseEntity.ok(new ApiResponse("ok", "Move applied", resp));
    }
}
