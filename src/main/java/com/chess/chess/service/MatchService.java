package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchService {
    private final MatchRepository matchRepo;
    private final BoardEntityRepository boardRepo;

    public MatchService(MatchRepository matchRepo, BoardEntityRepository boardRepo) {
        this.matchRepo = matchRepo;
        this.boardRepo = boardRepo;
    }

    public Match createMatch(User white, User black) {
        Match match = Match.builder()
                .whitePlayer(white)
                .blackPlayer(black)
                .status(GameStatus.WAITING)
                .build();
        match = matchRepo.save(match);

        // initialize board
        BoardEntity board = BoardEntity.builder()
                .match(match)
                .fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR") // starting FEN
                .build();
        boardRepo.save(board);

        return match;
    }

    public Optional<Match> findById(Long id) {
        return matchRepo.findById(id);
    }

    public List<Match> findByUser(User user) {
        return matchRepo.findByWhitePlayerOrBlackPlayer(user, user);
    }

    public Match save(Match match) {
        return matchRepo.save(match);
    }
}
