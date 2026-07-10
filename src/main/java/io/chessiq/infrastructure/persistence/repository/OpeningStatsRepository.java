package io.chessiq.infrastructure.persistence.repository;

import io.chessiq.infrastructure.persistence.entity.OpeningStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpeningStatsRepository extends JpaRepository<OpeningStatsEntity, UUID> {

    Optional<OpeningStatsEntity> findByPlayerIdAndEcoCodeAndColor(UUID playerId, String ecoCode, String color);

    List<OpeningStatsEntity> findByPlayerIdOrderByGamesPlayedDesc(UUID playerId);

    void deleteByPlayerId(UUID playerId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO opening_stats (
            id, player_id, eco_code, color,
            games_played, wins, losses, draws,
            avg_accuracy, accuracy_sample_count,
            last_played_at, updated_at
        )
        VALUES (
            gen_random_uuid(), :playerId, :ecoCode, :color,
            1,
            CASE WHEN :result = 'WIN' THEN 1 ELSE 0 END,
            CASE WHEN :result = 'LOSS' THEN 1 ELSE 0 END,
            CASE WHEN :result = 'DRAW' THEN 1 ELSE 0 END,
            :accuracy,
            CASE WHEN :accuracy IS NULL THEN 0 ELSE 1 END,
            :playedAt,
            NOW()
        )
        ON CONFLICT (player_id, eco_code, color)
        DO UPDATE SET
            games_played = opening_stats.games_played + 1,
            wins   = opening_stats.wins   + CASE WHEN :result = 'WIN'  THEN 1 ELSE 0 END,
            losses = opening_stats.losses + CASE WHEN :result = 'LOSS' THEN 1 ELSE 0 END,
            draws  = opening_stats.draws  + CASE WHEN :result = 'DRAW' THEN 1 ELSE 0 END,
            avg_accuracy = CASE
                WHEN opening_stats.avg_accuracy IS NULL AND :accuracy IS NULL
                    THEN NULL
                WHEN opening_stats.avg_accuracy IS NULL AND :accuracy IS NOT NULL
                    THEN :accuracy
                WHEN opening_stats.avg_accuracy IS NOT NULL AND :accuracy IS NULL
                    THEN opening_stats.avg_accuracy
                ELSE
                    (opening_stats.avg_accuracy * opening_stats.accuracy_sample_count + :accuracy)
                    / (opening_stats.accuracy_sample_count + 1)
            END,
            accuracy_sample_count = opening_stats.accuracy_sample_count
                + CASE WHEN :accuracy IS NULL THEN 0 ELSE 1 END,
            last_played_at = GREATEST(opening_stats.last_played_at, :playedAt),
            updated_at = NOW()
        """, nativeQuery = true)
    void upsertOpeningStats(
            @Param("playerId") UUID playerId,
            @Param("ecoCode") String ecoCode,
            @Param("color") String color,
            @Param("result") String result,
            @Param("accuracy") BigDecimal accuracy,
            @Param("playedAt") OffsetDateTime playedAt
    );
}