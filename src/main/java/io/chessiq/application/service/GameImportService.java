package io.chessiq.application.service;

import io.chessiq.infrastructure.chesscom.dto.ChessComGame;
import io.chessiq.infrastructure.persistence.entity.GameEntity;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.entity.SyncJobEntity;
import io.chessiq.infrastructure.persistence.repository.GameRepository;
import io.chessiq.infrastructure.persistence.repository.SyncJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameImportService {

    private final GameRepository gameRepository;
    private final SyncJobRepository syncJobRepository;
    private final GameParser gameParser;

    public GameImportService(GameRepository gameRepository,
                             SyncJobRepository syncJobRepository,
                             GameParser gameParser) {
        this.gameRepository = gameRepository;
        this.syncJobRepository = syncJobRepository;
        this.gameParser = gameParser;
    }

    @Transactional
    public void importMonth(PlayerEntity player, String archiveUrl, List<ChessComGame> games) {
        SyncJobEntity job = new SyncJobEntity();
        job.setPlayerId(player.getId());
        job.setArchiveUrl(archiveUrl);
        job.setStatus("RUNNING");
        job.setStartedAt(OffsetDateTime.now());
        job.setGamesFound(games.size());
        job = syncJobRepository.save(job);

        List<GameEntity> toSave = new ArrayList<>();
        for (ChessComGame game : games) {
            String gameId = extractGameId(game.url());
            boolean alreadyHave = gameRepository
                    .existsByPlayerIdAndChessComGameId(player.getId(), gameId);
            if (!alreadyHave) {
                toSave.add(gameParser.parse(game, player.getId(), player.getChessComUsername()));
            }
        }

        gameRepository.saveAll(toSave);

        job.setStatus("DONE");
        job.setGamesImported(toSave.size());
        job.setCompletedAt(OffsetDateTime.now());
        syncJobRepository.save(job);
    }

    private String extractGameId(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}