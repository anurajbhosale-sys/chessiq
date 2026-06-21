package io.chessiq.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="games")
@Getter
@Setter
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "player_id", nullable = false, updatable = false)
    private UUID playerId;

    @Column(name = "chess_com_game_id", nullable = false, length = 100)
    private String chessComGameId;

    @Column(name = "played_at", nullable = false)
    private OffsetDateTime playedAt;

    @Column(name = "time_class", nullable = false, length = 20)
    private String timeClass;

    @Column(name = "time_control", nullable = false, length = 20)
    private String timeControl;

    @Column(name = "player_color", nullable = false, length = 5)
    private String playerColor;

    @Column(name = "player_rating", nullable = false)
    private Integer playerRating;

    @Column(name = "opponent_username", nullable = false, length = 100)
    private String opponentUsername;

    @Column(name = "opponent_rating", nullable = false)
    private Integer opponentRating;

    @Column(name = "result", nullable = false, length = 20)
    private String result;

    @Column(name = "termination_reason", nullable = false, length = 30)
    private String terminationReason;

    @Column(name = "player_accuracy")
    private BigDecimal playerAccuracy;

    @Column(name = "opponent_accuracy")
    private BigDecimal opponentAccuracy;

    @Column(name = "eco_code", length = 10)
    private String ecoCode;

    @Column(name = "opening_name", length = 255)
    private String openingName;

    @Column(name = "opening_url", length = 500)
    private String openingUrl;

    @Column(name = "total_moves")
    private Integer totalMoves;

    @Column(name = "pgn", nullable = false, columnDefinition = "TEXT")
    private String pgn;

    @Column(name = "analysis_status", nullable = false, length = 20)
    private String analysisStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        if (this.analysisStatus == null) {
            this.analysisStatus = "PENDING";
        }
    }
}
