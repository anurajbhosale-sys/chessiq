package io.chessiq.application.service;

import io.chessiq.api.dto.response.WeaknessAnalysisResponse;
import io.chessiq.api.dto.response.WeaknessExplanation;
import io.chessiq.api.dto.response.WeaknessResponse;
import io.chessiq.infrastructure.anthropic.ClaudeClient;
import io.chessiq.infrastructure.persistence.entity.OpeningStatsEntity;
import io.chessiq.infrastructure.persistence.repository.OpeningStatsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WeaknessAnalysisService {

    private static final int MIN_GAMES_PLAYED = 15;
    private static final double MIN_GAP_PERCENTAGE_POINTS = 8.0;

    private final OpeningStatsRepository openingStatsRepository;
    private final ClaudeClient claudeClient;

    public WeaknessAnalysisService(OpeningStatsRepository openingStatsRepository,
                                   ClaudeClient claudeClient) {
        this.openingStatsRepository = openingStatsRepository;
        this.claudeClient = claudeClient;
    }

    public WeaknessAnalysisResponse analyzeWeaknesses(UUID playerId) {
        List<OpeningStatsEntity> allStats = openingStatsRepository.findByPlayerIdOrderByGamesPlayedDesc(playerId);

        int totalWins = 0;
        int totalGames = 0;
        for (OpeningStatsEntity stat : allStats) {
            totalWins += stat.getWins();
            totalGames += stat.getGamesPlayed();
        }
        double overallWinRate = (double) totalWins / totalGames * 100;

        List<OpeningStatsEntity> weaknesses = new ArrayList<>();
        for (OpeningStatsEntity stat : allStats) {
            if (stat.getGamesPlayed() < MIN_GAMES_PLAYED) continue;

            double openingWinRate = (double) stat.getWins() / stat.getGamesPlayed() * 100;
            if (overallWinRate - openingWinRate >= MIN_GAP_PERCENTAGE_POINTS) {
                weaknesses.add(stat);
            }
        }

        if (weaknesses.isEmpty()) {
            return new WeaknessAnalysisResponse(overallWinRate, List.of());
        }

        List<WeaknessExplanation> explanations = getExplanationsFromClaude(weaknesses, overallWinRate);
        List<WeaknessResponse> results = mergeResults(weaknesses, explanations);

        return new WeaknessAnalysisResponse(overallWinRate, results);
    }

    private List<WeaknessExplanation> getExplanationsFromClaude(List<OpeningStatsEntity> weaknesses, double overallWinRate) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("A chess player's overall win rate is ")
                .append(String.format("%.1f", overallWinRate))
                .append("%. Below are openings where they perform notably worse. ")
                .append("For each one, write a short (1-2 sentence) explanation of why this ")
                .append("might be a weak spot, based on the color and opening.\n\n");

        for (OpeningStatsEntity stat : weaknesses) {
            double winRate = (double) stat.getWins() / stat.getGamesPlayed() * 100;
            prompt.append("- ECO ").append(stat.getEcoCode())
                    .append(", playing ").append(stat.getColor())
                    .append(": ").append(String.format("%.1f", winRate)).append("% win rate over ")
                    .append(stat.getGamesPlayed()).append(" games\n");
        }

        return claudeClient.sendStructuredMessage(prompt.toString());
    }

    private List<WeaknessResponse> mergeResults(List<OpeningStatsEntity> weaknesses, List<WeaknessExplanation> explanations) {
        List<WeaknessResponse> results = new ArrayList<>();

        for (OpeningStatsEntity stat : weaknesses) {
            String explanationText = explanations.stream()
                    .filter(e -> e.ecoCode().equals(stat.getEcoCode()) && e.color().equals(stat.getColor()))
                    .findFirst()
                    .map(WeaknessExplanation::explanation)
                    .orElse("No explanation available.");

            double winRate = (double) stat.getWins() / stat.getGamesPlayed() * 100;

            results.add(new WeaknessResponse(
                    stat.getEcoCode(),
                    stat.getColor(),
                    stat.getGamesPlayed(),
                    winRate,
                    explanationText
            ));
        }

        return results;
    }
}