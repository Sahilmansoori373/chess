package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_entities")
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

    @Lob
    @Column(nullable = false)
    private String fen;

    @Lob
    private String moveHistory;

    @Enumerated(EnumType.STRING)
    private Color turnColor = Color.WHITE;
}
