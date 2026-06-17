package io.chessiq.infrastructure.persistence.repository;

import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {

  Optional<PlayerEntity> findByChessComUsername(String chessComUsername);

  boolean  existsByChessComUsername(String chessComUsername);
}
