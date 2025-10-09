package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_states")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardState {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id", unique = true)
    private Match match;

    // Store the board layout as FEN string or JSON
    @Lob
    @Column(nullable = false)
    private String fen; // e.g., "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"

    // Optional PGN history for replay
    @Lob
    private String moveHistory;
}
