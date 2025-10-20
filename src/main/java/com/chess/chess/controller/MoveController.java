package com.chess.chess.controller;

import com.chess.chess.dto.*;
import com.chess.chess.exception.*;
import com.chess.chess.game.model.MoveResult;
import com.chess.chess.game.service.GameEngineService;
import com.chess.chess.model.*;
import com.chess.chess.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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

    public MoveController(MatchService matchService,
                          BoardStateService boardStateService,
                          MoveService moveService,
                          GameEngineService gameEngineService,
                          UserService userService,
                          UserScoreHistoryService scoreHistoryService,
                          MessageService messageService) {
        this.matchService = matchService;
        this.boardStateService = boardStateService;
        this.moveService = moveService;
        this.gameEngineService = gameEngineService;
        this.userService = userService;
        this.scoreHistoryService = scoreHistoryService;
        this.messageService = messageService;
    }

    /**
     * Process a move from the authenticated user.
     */
    @PostMapping("/play")
    @Transactional
    public ResponseEntity<ApiResponse> playMove(
            @Validated @RequestBody MoveRequest req,
            @RequestHeader("X-USER-ID") Long userId,
            Principal principal) {

        // 1️⃣ Load match and validate status
        Match match = matchService.findById(req.matchId())
                .orElseThrow(() -> new NotFoundException("Match not found"));

        if (match.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ConflictException("Match is not in progress");
        }

        // 2️⃣ Load player and determine color
        User player = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Color playerColor =
                match.getWhitePlayer().getId().equals(player.getId()) ? Color.WHITE :
                        match.getBlackPlayer().getId().equals(player.getId()) ? Color.BLACK : null;

        if (playerColor == null) {
            throw new ConflictException("Player is not part of this match");
        }

        // 3️⃣ Enforce turn
        int movesSoFar = match.getMoves().size();
        Color expected = (movesSoFar % 2 == 0) ? Color.WHITE : Color.BLACK;
        if (expected != playerColor) {
            throw new ConflictException("Not your turn");
        }

        // 4️⃣ Load board state
        BoardEntity boardState = boardStateService.findByMatch(match)
                .orElseThrow(() -> new NotFoundException("Board state not found"));

        // 5️⃣ Process move using GameEngineService
        MoveResult result = gameEngineService.processMove(
                match.getId(),
                req.from(),
                req.to(),
                principal.getName() // now properly injected
        );

        // 6️⃣ Persist move entity
        Move moveEntity = Move.builder()
                .match(match)
                .player(player)
                .fromPosition(req.from())
                .toPosition(req.to())
                .piece(result.getNotation())
                .capturedPiece(result.getCapturedPiece())
                .moveNumber(match.getMoves().size() + 1)
                .moveNotation(result.getNotation())
                .color(playerColor)
                .build();

        moveService.saveMove(moveEntity);
        boardState.setFen(result.getNewFen());
        boardStateService.save(boardState);
        match.getMoves().add(moveEntity);

        // 7️⃣ Handle endgame (checkmate / stalemate)
        if (result.isCheckmate() || result.isStalemate()) {
            match.setStatus(GameStatus.COMPLETED);

            if (result.getWinner() != null) {
                User winner = (result.getWinner() == Color.WHITE)
                        ? match.getWhitePlayer()
                        : match.getBlackPlayer();
                User loser = (winner == match.getWhitePlayer())
                        ? match.getBlackPlayer()
                        : match.getWhitePlayer();

                int deltaWin = 30, deltaLose = 20;
                int beforeWinner = winner.getScore();
                int beforeLoser = loser.getScore();

                winner.setScore(winner.getScore() + deltaWin);
                loser.setScore(loser.getScore() - deltaLose);

                matchService.save(match);
                // Log score changes
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

                // Send system message
                messageService.sendMessage(Message.builder()
                        .match(match)
                        .sender(null)
                        .content("Game over. Winner: " + winner.getUsername())
                        .build());
            }
            matchService.save(match);
        } else {
            matchService.save(match);
        }

        // 8️⃣ Build response
        Map<String, Object> resp = new HashMap<>();
        resp.put("fen", result.getNewFen());
        resp.put("notation", result.getNotation());
        resp.put("check", result.isCheck());
        resp.put("checkmate", result.isCheckmate());
        resp.put("stalemate", result.isStalemate());
        resp.put("captured", result.getCapturedPiece());

        return ResponseEntity.ok(new ApiResponse("ok", "Move applied successfully", resp));
    }
}
