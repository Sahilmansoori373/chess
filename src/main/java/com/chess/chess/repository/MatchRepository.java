package com.chess.chess.repository;

import com.chess.chess.model.Match;
import com.chess.chess.model.User;
import com.chess.chess.model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByWhitePlayerOrBlackPlayer(User white, User black);
    List<Match> findByStatus(GameStatus status);
}
