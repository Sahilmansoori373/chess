package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_score_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserScoreHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    private int scoreBefore;
    private int scoreAfter;

    @ManyToOne @JoinColumn(name = "match_id")
    private Match match;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date timestamp = new java.util.Date();
}
