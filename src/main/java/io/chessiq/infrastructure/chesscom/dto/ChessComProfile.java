package io.chessiq.infrastructure.chesscom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComProfile(
        @JsonProperty("player_id") Long playerId,
        String username,
        String name,
        String country,
        Long joined
) {


}