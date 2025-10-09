package com.chess.chess.repository;

import com.chess.chess.model.Message;
import com.chess.chess.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByMatchOrderByTimestampAsc(Match match);
}
