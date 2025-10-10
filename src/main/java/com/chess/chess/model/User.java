package com.chess.chess.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Version
    private Long version;

    private int score = 1000; // ELO-like score

    // Matches played as white
    @OneToMany(mappedBy = "whitePlayer", cascade = CascadeType.ALL)
    private List<Match> whiteMatches = new ArrayList<>();

    // Matches played as black
    @OneToMany(mappedBy = "blackPlayer", cascade = CascadeType.ALL)
    private List<Match> blackMatches = new ArrayList<>();

    // Messages sent
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    // Moves made
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Move> moves = new ArrayList<>();

    // Rating history
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserScoreHistory> scoreHistory = new ArrayList<>();

    // Friends (optional social feature)
    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();
}
