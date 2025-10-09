package com.chess.chess.repository;

import com.chess.chess.model.UserScoreHistory;
import com.chess.chess.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserScoreHistoryRepository extends JpaRepository<UserScoreHistory, Long> {
    List<UserScoreHistory> findByUserOrderByTimestampDesc(User user);
}
