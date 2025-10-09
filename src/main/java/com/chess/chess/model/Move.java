package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "moves")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Move {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne @JoinColumn(name = "player_id")
    private User player;

    private String fromPosition;   // e.g., "e2"
    private String toPosition;     // e.g., "e4"
    private String piece;          // "Pawn", "Queen", etc.
    private String capturedPiece;  // null if none
    private int moveNumber;
    private String moveNotation;   // e.g., "e4", "Nf3"

    @Enumerated(EnumType.STRING)
    private Color color;
}
