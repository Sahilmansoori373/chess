package com.chess.chess.service;

import com.chess.chess.model.*;
import com.chess.chess.repository.UserScoreHistoryRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserScoreHistoryService {
    private final UserScoreHistoryRepository scoreRepo;

    public UserScoreHistoryService(UserScoreHistoryRepository scoreRepo) {
        this.scoreRepo = scoreRepo;
    }

    public List<UserScoreHistory> getByUser(User user) {
        return scoreRepo.findByUserOrderByTimestampDesc(user);
    }

    public UserScoreHistory save(UserScoreHistory history) {
        return scoreRepo.save(history);
    }
}
