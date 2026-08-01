package io.chessiq.application.service;

import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.domain.exception.PlayerAccessDeniedException;
import io.chessiq.domain.exception.PlayerAlreadyExistsException;
import io.chessiq.domain.exception.PlayerNotFoundException;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PlayerService {



    private final PlayerRepository playerRepository;
    private final AggregationService aggregationService;

    public PlayerService(PlayerRepository playerRepository, AggregationService aggregationService) {
        this.playerRepository = playerRepository;
        this.aggregationService = aggregationService;
    }

    @Transactional
    public PlayerResponse registerPlayer(RegisterPlayerRequest request, UUID currentUserId) {
        String username = request.chessComUsername();

        if (playerRepository.existsByChessComUsername(username)) {
            throw new PlayerAlreadyExistsException(username);
        }

        PlayerEntity player = new PlayerEntity();
        player.setChessComUsername(username);
        player.setUserId(currentUserId);   // now refers to the parameter

        PlayerEntity saved = playerRepository.save(player);
        return toResponse(saved);
    }


    public void rebuildStats(String username, UUID currentUserId) {
        PlayerEntity player = findOwnedPlayer(username, currentUserId);
        aggregationService.rebuildOpeningStats(player.getId());
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayer(String username) {
        PlayerEntity player = playerRepository.findByChessComUsername(username)
                .orElseThrow(() -> new PlayerNotFoundException(username));
        return toResponse(player);
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

    public PlayerEntity findOwnedPlayer(String username, UUID currentUserId) {
        PlayerEntity player = playerRepository.findByChessComUsername(username)
                .orElseThrow(() -> new PlayerNotFoundException(username));
        if (!player.getUserId().equals(currentUserId)) {
            throw new PlayerAccessDeniedException(username);
        }
        return player;
    }
}