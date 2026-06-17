package io.chessiq.api.controller;


import io.chessiq.api.dto.request.RegisterPlayerRequest;
import io.chessiq.api.dto.response.PlayerResponse;
import io.chessiq.application.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;


    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> registerPlayer(
            @Valid @RequestBody RegisterPlayerRequest request){
        PlayerResponse response = playerService.registerPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
