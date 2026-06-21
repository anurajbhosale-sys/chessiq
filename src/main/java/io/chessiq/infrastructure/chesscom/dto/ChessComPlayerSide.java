package io.chessiq.infrastructure.chesscom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComPlayerSide(
        Integer rating,
        String result,
        String username
) {}