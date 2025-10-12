package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.BoardEntityRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BoardStateService {
    private final BoardEntityRepository boardRepo;

    public BoardStateService(BoardEntityRepository boardRepo) {
        this.boardRepo = boardRepo;
    }

    public Optional<BoardEntity> findByMatch(Match match) {
        return boardRepo.findByMatch(match);
    }

    public BoardEntity save(BoardEntity boardState) {
        return boardRepo.save(boardState);
    }
}
