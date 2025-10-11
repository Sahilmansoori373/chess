package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id", unique = true)
    private Match match;

    // Stores the current layout of the board in FEN format
    @Lob
    @Column(nullable = false)
    private String fen;

    // Optional PGN or move history
    @Lob
    private String moveHistory;

    // Optionally track which player's turn it is (useful for resuming games)
    @Enumerated(EnumType.STRING)
    @Column(name = "turn_color")
    private Color turnColor;
}
