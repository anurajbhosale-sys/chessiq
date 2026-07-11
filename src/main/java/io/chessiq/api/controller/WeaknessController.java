package io.chessiq.api.controller;

import io.chessiq.api.dto.response.WeaknessAnalysisResponse;
import io.chessiq.application.service.WeaknessAnalysisService;
import io.chessiq.infrastructure.persistence.entity.PlayerEntity;
import io.chessiq.infrastructure.persistence.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class WeaknessController {

    private final WeaknessAnalysisService weaknessAnalysisService;
    private final PlayerRepository playerRepository;

    public WeaknessController(WeaknessAnalysisService weaknessAnalysisService,
                              PlayerRepository playerRepository) {
        this.weaknessAnalysisService = weaknessAnalysisService;
        this.playerRepository = playerRepository;
    }

    @GetMapping("/{username}/weaknesses")
    public ResponseEntity<WeaknessAnalysisResponse> getWeaknesses(@PathVariable String username) {
        PlayerEntity player = playerRepository.findByChessComUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not registered: " + username));

        WeaknessAnalysisResponse response = weaknessAnalysisService.analyzeWeaknesses(player.getId());

        return ResponseEntity.ok(response);
    }
}