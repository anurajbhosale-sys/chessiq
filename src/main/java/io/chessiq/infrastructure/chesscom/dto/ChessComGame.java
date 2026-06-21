package io.chessiq.infrastructure.chesscom.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComGame( String url,
                            String pgn,
                            @JsonProperty("time_control") String timeControl,
                            @JsonProperty("end_time") Long endTime,
                            Boolean rated,
                            @JsonProperty("time_class") String timeClass,
                            String eco,
                            ChessComPlayerSide white,
                            ChessComPlayerSide black,
                            ChessComAccuracies accuracies) {



}
