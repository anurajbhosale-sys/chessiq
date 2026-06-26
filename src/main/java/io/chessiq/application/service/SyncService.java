package io.chessiq.application.service;

import io.chessiq.infrastructure.chesscom.ChessComClient;
import io.chessiq.infrastructure.chesscom.dto.ChessComGame;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final PlayerRepository playerRepository;
    private final ChessComClient chessComClient;
    private final GameImportService gameImportService;

    public SyncService(PlayerRepository playerRepository,
                       ChessComClient chessComClient,
                       GameImportService gameImportService) {
        this.playerRepository = playerRepository;
        this.chessComClient = chessComClient;
        this.gameImportService = gameImportService;
    }

    @Async
    public void syncPlayer(String username) {
        PlayerEntity player = playerRepository.findByChessComUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not registered: " + username));

        log.info("Starting sync for player {}", username);
        player.setSyncStatus("SYNCING");
        playerRepository.save(player);

        List<String> archiveUrls = chessComClient.fetchArchives(username);
        log.info("Found {} archive months for {}", archiveUrls.size(), username);

        int monthsSucceeded = 0;
        int monthsFailed = 0;

        for (String archiveUrl : archiveUrls) {
            try {
                List<ChessComGame> games = chessComClient.fetchGamesForMonth(archiveUrl);
                gameImportService.importMonth(player, archiveUrl, games);
                monthsSucceeded++;
            } catch (Exception ex) {
                monthsFailed++;
                log.error("Failed to import month {}: {}", archiveUrl, ex.getMessage(), ex);
            }
        }

        player.setSyncStatus(monthsFailed == 0 ? "SYNCED" : "FAILED");
        playerRepository.save(player);

        log.info("Sync complete for {}: {} months succeeded, {} failed",
                username, monthsSucceeded, monthsFailed);
    }
}