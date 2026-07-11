package io.chessiq.api.dto.response;

public record WeaknessResponse(
        String ecoCode,
        String color,
        int gamesPlayed,
        double winRatePercent,
        String explanation
) {}