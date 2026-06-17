package io.chessiq.api.dto.response;

import java.util.UUID;

public record PlayerResponse(
        UUID id,
        String chessComUsername,
        String displayName,
        String countryCode,
        String syncStatus,
        Integer rapidRatingCurrent,
        Integer rapidRatingBest,
        Integer rapidWins,
        Integer rapidLosses,
        Integer rapidDraws,
        Integer tacticsRatingBest
) {}