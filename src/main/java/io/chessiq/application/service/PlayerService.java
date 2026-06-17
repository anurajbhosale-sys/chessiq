package io.chessiq.application.service;

import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.domain.exception.PlayerAlreadyExistsException;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public PlayerResponse registerPlayer(RegisterPlayerRequest request) {
        String username = request.chessComUsername();

        // Step 1 + 2 — your guard: reject if already exists
        if (playerRepository.existsByChessComUsername(username)) {
            throw new PlayerAlreadyExistsException(username);
        }

        // Step 3 — build the entity from the request
        PlayerEntity player = new PlayerEntity();
        player.setChessComUsername(username);

        // Step 4 — save it
        PlayerEntity saved = playerRepository.save(player);

        // Step 5 + 6 — convert entity to response DTO and return
        return toResponse(saved);
    }

    private PlayerResponse toResponse(PlayerEntity entity) {
        return new PlayerResponse(
                entity.getId(),
                entity.getChessComUsername(),
                entity.getDisplayName(),
                entity.getCountryCode(),
                entity.getSyncStatus(),
                entity.getRapidRatingCurrent(),
                entity.getRapidRatingBest(),
                entity.getRapidWins(),
                entity.getRapidLosses(),
                entity.getRapidDraws(),
                entity.getTacticsRatingBest()
        );
    }
}