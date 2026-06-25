package io.chessiq.infrastructure.chesscom;

import io.chessiq.config.ChessComProperties;
import io.chessiq.infrastructure.chesscom.dto.ChessComArchives;
import io.chessiq.infrastructure.chesscom.dto.ChessComGame;
import io.chessiq.infrastructure.chesscom.dto.ChessComMonthGames;
import io.chessiq.infrastructure.chesscom.dto.ChessComProfile;
import io.chessiq.infrastructure.chesscom.exception.PlayerNotFoundOnChessComException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ChessComClient {

    private final RestClient restClient;

    public ChessComClient(ChessComProperties properties){
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("User_Agent", properties.userAgent())
                .build();
    }

    public ChessComProfile fetchProfile(String username){
        try{
            return restClient.get().
                    uri("/player/{username}", username)
                    .retrieve()
                    .body(ChessComProfile.class);
        }catch(HttpClientErrorException ex){
            throw new PlayerNotFoundOnChessComException(username);

        }
    }

    public List<ChessComGame> fetchGamesForMonth(String archiveUrl) {
        ChessComMonthGames response = restClient.get()
                .uri(archiveUrl)
                .retrieve()
                .body(ChessComMonthGames.class);

        return response != null ? response.games() : List.of();
    }

    public List<String> fetchArchives(String username) {
        ChessComArchives response = restClient.get()
                .uri("/player/{username}/games/archives", username)
                .retrieve()
                .body(ChessComArchives.class);

        return response != null ? response.archives() : List.of();
    }
}
