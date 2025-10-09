package com.chess.chess.repository;

import com.chess.chess.model.BoardState;
import com.chess.chess.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardStateRepository extends JpaRepository<BoardState, Long> {
    Optional<BoardState> findByMatch(Match match);
}
