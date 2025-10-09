package com.chess.chess.repository;

import com.chess.chess.model.Move;
import com.chess.chess.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findByMatchOrderByMoveNumberAsc(Match match);
}
