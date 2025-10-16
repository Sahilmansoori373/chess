package com.chess.chess.model;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "white_player_id", nullable = false)
    private User whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id", nullable = false)
    private User blackPlayer;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner; // Null until game ends

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "turn_color")
    private Color turnColor = Color.WHITE; // Default: white starts

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Move> moves = new ArrayList<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private BoardEntity boardEntity;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -------------------------------------
    // 🔹 Helper Methods for Game Logic
    // -------------------------------------

    /**
     * Returns the player corresponding to the given color.
     */
    public User getPlayerByColor(Color color) {
        return color == Color.WHITE ? whitePlayer : blackPlayer;
    }

    /**
     * Sets the winner of the match.
     */
    public void setWinner(User winner) {
        this.winner = winner;
    }

    /**
     * Updates the match status safely.
     */
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    /**
     * Switches the turn color after a valid move.
     */
    public void switchTurn() {
        this.turnColor = (this.turnColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
}
