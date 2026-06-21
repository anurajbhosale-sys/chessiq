package io.chessiq.infrastructure.chesscom.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComArchives(List<String> archives) {


}
