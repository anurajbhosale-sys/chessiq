package io.chessiq.infrastructure.persistence.repository;

import io.chessiq.infrastructure.persistence.entity.SyncJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJobEntity, UUID> {

    List<SyncJobEntity> findByPlayerIdOrderByCreatedAtDesc(UUID playerId);
}