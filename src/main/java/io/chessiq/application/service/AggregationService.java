package io.chessiq.application.service;

import io.chessiq.infrastructure.persistence.entity.GameEntity;
import io.chessiq.infrastructure.persistence.repository.GameRepository;
import io.chessiq.infrastructure.persistence.repository.OpeningStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AggregationService {

    private final OpeningStatsRepository openingStatsRepository;
    private final GameRepository gameRepository;

    public AggregationService(OpeningStatsRepository openingStatsRepository,
                              GameRepository gameRepository) {
        this.openingStatsRepository = openingStatsRepository;
        this.gameRepository = gameRepository;
    }

    public void upsertOpeningStats(GameEntity game) {
        openingStatsRepository.upsertOpeningStats(
                game.getPlayerId(),
                game.getEcoCode(),
                game.getPlayerColor(),
                game.getResult(),
                game.getPlayerAccuracy(),
                game.getPlayedAt()
        );
    }

    @Transactional
    public void rebuildOpeningStats(UUID playerId) {
        openingStatsRepository.deleteByPlayerId(playerId);

        List<GameEntity> games = gameRepository.findByPlayerId(playerId);
        for (GameEntity game : games) {
            upsertOpeningStats(game);
        }
    }


}