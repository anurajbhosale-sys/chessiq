package io.chessiq.api.controller;

import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.application.service.PlayerService;
import io.chessiq.application.service.SyncService;
import io.chessiq.infrastructure.chesscom.ChessComClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody RegisterPlayerRequest request){
        PlayerResponse response = playerService.registerPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{username}/sync")
    public ResponseEntity<Void> syncPlayer(@PathVariable String username) {
        syncService.syncPlayer(username);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{username}/rebuild-stats")
    public ResponseEntity<Void> rebuildStats(@PathVariable String username) {
        playerService.rebuildStats(username);
        return ResponseEntity.ok().build();
    }
}