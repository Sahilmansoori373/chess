package com.chess.chess.controller;

import com.chess.chess.dto.*;
import com.chess.chess.exception.NotFoundException;
import com.chess.chess.model.*;
import com.chess.chess.service.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final MatchService matchService;
    private final UserService userService;
    private final MessageService messageService;

    public ChatController(MatchService matchService, UserService userService, MessageService messageService) {
        this.matchService = matchService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMessage(@Validated @RequestBody ChatRequest req, @RequestHeader("X-USER-ID") Long userId) {
        Match match = matchService.findById(req.matchId()).orElseThrow(() -> new NotFoundException("Match not found"));
        User sender = userService.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (!match.getWhitePlayer().getId().equals(userId) && !match.getBlackPlayer().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("error", "Not a participant", null));
        }

        Message m = Message.builder()
                .match(match)
                .sender(sender)
                .content(req.content())
                .build();
        Message saved = messageService.sendMessage(m);
        return ResponseEntity.ok(new ApiResponse("ok", "Message sent", saved));
    }

    @GetMapping("/history/{matchId}")
    public ResponseEntity<ApiResponse> getHistory(@PathVariable Long matchId) {
        Match match = matchService.findById(matchId).orElseThrow(() -> new NotFoundException("Match not found"));
        var history = messageService.getMessagesByMatch(match);
        return ResponseEntity.ok(new ApiResponse("ok", "Messages", history));
    }
}
