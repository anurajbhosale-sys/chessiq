package io.chessiq.infrastructure.persistence.repository;


import io.chessiq.infrastructure.persistence.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, UUID> {

    boolean existsByPlayerIdAndChessComGameId(UUID playerId, String ChessComGameId);

    long countByPlayerId(UUID playerId);
}
