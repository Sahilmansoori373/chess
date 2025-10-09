package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.MoveRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MoveService {
    private final MoveRepository moveRepo;

    public MoveService(MoveRepository moveRepo) {
        this.moveRepo = moveRepo;
    }

    public Move saveMove(Move move) {
        return moveRepo.save(move);
    }

    public List<Move> getMovesByMatch(Match match) {
        return moveRepo.findByMatchOrderByMoveNumberAsc(match);
    }
}
