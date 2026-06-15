package io.chessiq.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "chess_com_username", nullable = false, unique = true, length = 100)
    private String chessComUsername;

    @Column(name = "chess_com_id", unique = true)
    private Long chessComId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "joined_chess_com_at")
    private OffsetDateTime joinedChessComAt;

    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt;

    @Column(name = "sync_status", nullable = false, length = 20)
    private String syncStatus;

    @Column(name = "rapid_rating_current")
    private Integer rapidRatingCurrent;

    @Column(name = "rapid_rating_best")
    private Integer rapidRatingBest;

    @Column(name = "rapid_wins", nullable = false)
    private Integer rapidWins;

    @Column(name = "rapid_losses", nullable = false)
    private Integer rapidLosses;

    @Column(name = "rapid_draws", nullable = false)
    private Integer rapidDraws;

    @Column(name = "tactics_rating_best")
    private Integer tacticsRatingBest;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.syncStatus == null) {
            this.syncStatus = "NEVER_SYNCED";
        }
        if (this.rapidWins == null) this.rapidWins = 0;
        if (this.rapidLosses == null) this.rapidLosses = 0;
        if (this.rapidDraws == null) this.rapidDraws = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}