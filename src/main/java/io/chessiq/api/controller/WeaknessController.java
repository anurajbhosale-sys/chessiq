package io.chessiq.api.controller;

import io.chessiq.api.dto.response.WeaknessAnalysisResponse;
import io.chessiq.application.service.PlayerService;
import io.chessiq.application.service.WeaknessAnalysisService;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class WeaknessController {

    private final WeaknessAnalysisService weaknessAnalysisService;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    public WeaknessController(WeaknessAnalysisService weaknessAnalysisService,
                              PlayerRepository playerRepository, PlayerService playerService) {
        this.weaknessAnalysisService = weaknessAnalysisService;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    @GetMapping("/{username}/weaknesses")
    public ResponseEntity<WeaknessAnalysisResponse> getWeaknesses(
            @PathVariable String username,
            @AuthenticationPrincipal UUID currentUserId) {

        PlayerEntity player = playerService.findOwnedPlayer(username, currentUserId);

        WeaknessAnalysisResponse response = weaknessAnalysisService.analyzeWeaknesses(player.getId());

        return ResponseEntity.ok(response);
    }
}