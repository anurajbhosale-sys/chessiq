package io.chessiq.api.dto.response;

public record WeaknessExplanation(
        String ecoCode,
        String color,
        String explanation
) {}