package io.chessiq.infrastructure.persistence.repository;

import io.chessiq.infrastructure.persistence.entity.OpeningStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpeningStatsRepository extends JpaRepository<OpeningStatsEntity, UUID> {

    Optional<OpeningStatsEntity> findByPlayerIdAndEcoCodeAndColor(UUID playerId, String ecoCode, String color);

    List<OpeningStatsEntity> findByPlayerIdOrderByGamesPlayedDesc(UUID playerId);

    void deleteByPlayerId(UUID playerId);
}