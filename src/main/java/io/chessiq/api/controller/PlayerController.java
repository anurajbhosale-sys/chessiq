package io.chessiq.api.controller;


import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.application.service.PlayerService;
import io.chessiq.infrastructure.chesscom.ChessComClient;
import io.chessiq.infrastructure.chesscom.dto.ChessComGame;
import io.chessiq.infrastructure.chesscom.dto.ChessComProfile;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;
    private final ChessComClient chessComClient;   // add this field

    public PlayerController(PlayerService playerService, ChessComClient chessComClient) {
        this.playerService = playerService;
        this.chessComClient = chessComClient;       // add this line
    }


    @PostMapping
    public ResponseEntity<PlayerResponse> registerPlayer(
            @Valid @RequestBody RegisterPlayerRequest request){
        PlayerResponse response = playerService.registerPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TEMPORARY — for testing the Chess.com client. Delete after verifying.
    @GetMapping("/test-fetch/{username}")
    public ChessComProfile testFetch(@PathVariable String username) {
        return chessComClient.fetchProfile(username);
    }

    // TEMPORARY — testing game fetch. Delete after verifying.
    @GetMapping("/test-games")
    public List<ChessComGame> testGames() {
        String mayArchive = "https://api.chess.com/pub/player/anuraj1212/games/2026/05";
        return chessComClient.fetchGamesForMonth(mayArchive);
    }
}
