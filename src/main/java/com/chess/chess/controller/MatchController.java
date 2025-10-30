package com.chess.chess.controller;

import com.chess.chess.dto.*;
import com.chess.chess.exception.NotFoundException;
import com.chess.chess.model.*;
import com.chess.chess.service.*;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final UserService userService;
    private final MatchService matchService;
    private final BoardStateService boardStateService;

    public MatchController(UserService userService, MatchService matchService, BoardStateService boardStateService) {
        this.userService = userService;
        this.matchService = matchService;
        this.boardStateService = boardStateService;
    }

    // Start match: current user starts a match against opponent
    @PostMapping("/start")
    @Transactional
    public ResponseEntity<ApiResponse> startMatch(@Validated @RequestBody StartMatchRequest request, @RequestHeader("X-USER-ID") Long starterId) {
        User starter = userService.findById(starterId).orElseThrow(() -> new NotFoundException("Starter not found"));
        User opponent = userService.findById(request.opponentId()).orElseThrow(() -> new NotFoundException("Opponent not found"));

        Match match = matchService.createMatch(starter, opponent);

        // Optionally mark match in-progress immediately or wait for acceptance
        match.setStatus(GameStatus.IN_PROGRESS);
        matchService.save(match);
        var boardState = boardStateService.findByMatch(match).orElseThrow(() -> new NotFoundException("BoardState missing"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("ok", "Match started", Map.of("matchId", match.getId(), "fen", boardState.getFen())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMatch(@PathVariable Long id) {
        Match match = matchService.findById(id).orElseThrow(() -> new NotFoundException("Match not found"));
        return ResponseEntity.ok(new ApiResponse("ok", "Match fetched", match));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getMatchesByUser(@PathVariable Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Match> matches = matchService.findByUser(user);
        return ResponseEntity.ok(new ApiResponse("ok", "Matches", matches));
    }
}
