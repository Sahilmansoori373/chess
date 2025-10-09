package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.BoardStateRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BoardStateService {
    private final BoardStateRepository boardRepo;

    public BoardStateService(BoardStateRepository boardRepo) {
        this.boardRepo = boardRepo;
    }

    public Optional<BoardState> findByMatch(Match match) {
        return boardRepo.findByMatch(match);
    }

    public BoardState save(BoardState boardState) {
        return boardRepo.save(boardState);
    }
}
