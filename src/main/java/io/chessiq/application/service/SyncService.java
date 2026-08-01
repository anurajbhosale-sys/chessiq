package io.chessiq.application.service;

import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SyncService {

    private final PlayerService playerService;
    private final SyncWorker syncWorker;

    public SyncService(PlayerService playerService, SyncWorker syncWorker) {
        this.playerService = playerService;
        this.syncWorker = syncWorker;
    }

    // The DOOR: synchronous, runs in the request thread.
    // 403/404 thrown here still reach the HTTP response.
    public void requestSync(String username, UUID currentUserId) {
        PlayerEntity player = playerService.findOwnedPlayer(username, currentUserId);
        syncWorker.doSync(player);
    }
}