package io.chessiq.infrastructure.chesscom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComAccuracies(
        Double white,
        Double black
) {}