package com.chess.chess.repository;

import com.chess.chess.model.BoardEntity;
import com.chess.chess.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardEntityRepository extends JpaRepository<BoardEntity, Long> {
    Optional<BoardEntity> findByMatch(Match match);
}
