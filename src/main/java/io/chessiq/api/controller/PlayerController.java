package io.chessiq.api.controller;

import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.application.service.PlayerService;
import io.chessiq.application.service.SyncService;
import io.chessiq.infrastructure.chesscom.ChessComClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;
    private final ChessComClient chessComClient;
    private final SyncService syncService;

    public PlayerController(PlayerService playerService, ChessComClient chessComClient, SyncService syncService) {
        this.playerService = playerService;
        this.chessComClient = chessComClient;
        this.syncService = syncService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> registerPlayer(
            @Valid @RequestBody RegisterPlayerRequest request,
            @AuthenticationPrincipal UUID currentUserId) {
        PlayerResponse response = playerService.registerPlayer(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{username}/sync")
    public ResponseEntity<Void> syncPlayer(
            @PathVariable String username,
            @AuthenticationPrincipal UUID currentUserId) {
        syncService.requestSync(username, currentUserId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{username}/rebuild-stats")
    public ResponseEntity<Void> rebuildStats(
            @PathVariable String username,
            @AuthenticationPrincipal UUID currentUserId) {
        playerService.rebuildStats(username, currentUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable String username) {
        return ResponseEntity.ok(playerService.getPlayer(username));
    }
}