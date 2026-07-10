package io.chessiq.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "opening_stats")
public class OpeningStatsEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "eco_code", nullable = false)
    private String ecoCode;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "games_played", nullable = false)
    private int gamesPlayed;

    @Column(name = "wins", nullable = false)
    private int wins;

    @Column(name = "losses", nullable = false)
    private int losses;

    @Column(name = "draws", nullable = false)
    private int draws;

    @Column(name = "avg_accuracy")
    private BigDecimal avgAccuracy;

    @Column(name = "accuracy_sample_count", nullable = false)
    private int accuracySampleCount;

    @Column(name = "last_played_at")
    private OffsetDateTime lastPlayedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected OpeningStatsEntity() {
        // JPA
    }

    public OpeningStatsEntity(UUID playerId, String ecoCode, String color) {
        this.playerId = playerId;
        this.ecoCode = ecoCode;
        this.color = color;
    }

    public UUID getId() { return id; }
    public UUID getPlayerId() { return playerId; }
    public String getEcoCode() { return ecoCode; }
    public String getColor() { return color; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getDraws() { return draws; }
    public BigDecimal getAvgAccuracy() { return avgAccuracy; }
    public int getAccuracySampleCount() { return accuracySampleCount; }
    public OffsetDateTime getLastPlayedAt() { return lastPlayedAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}