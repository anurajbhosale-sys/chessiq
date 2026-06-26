package io.chessiq.application.service;


import io.chessiq.infrastructure.chesscom.dto.ChessComGame;
import io.chessiq.infrastructure.chesscom.dto.ChessComPlayerSide;
import io.chessiq.infrastructure.persistence.entity.GameEntity;
import io.chessiq.infrastructure.persistence.repository.GameRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class GameParser {

    private final GameRepository repository;

    public GameParser(GameRepository repository) {
        this.repository = repository;
    }

    public GameEntity parse(ChessComGame game, UUID playerId, String username){
        boolean iAmWhite = game.white().username().equalsIgnoreCase(username);
        ChessComPlayerSide mySide = iAmWhite ? game.white() : game.black();
        ChessComPlayerSide opponentSide = iAmWhite ? game.black() : game.white();

        GameEntity entity = new GameEntity();
        entity.setPlayerId(playerId);
        entity.setChessComGameId(extractGameId(game.url()));
        entity.setPlayedAt(toOffsetDateTime(game.endTime()));
        entity.setTimeClass(game.timeClass());
        entity.setTimeControl(game.timeControl());
        entity.setPlayerColor(iAmWhite ? "WHITE" : "BLACK");
        entity.setPlayerRating(mySide.rating());
        entity.setOpponentUsername(opponentSide.username());
        entity.setOpponentRating(opponentSide.rating());
        entity.setResult(bucketResult(mySide.result()));
        entity.setTerminationReason(mySide.result());
        entity.setOpeningUrl(game.eco());
        entity.setOpeningName(extractOpeningName(game.eco()));
        entity.setPgn(game.pgn());
        entity.setTotalMoves(countMoves(game.pgn()));
        entity.setEcoCode(extractEcoCode(game.pgn()));

        if (game.accuracies() != null) {
            Double myAcc = iAmWhite ? game.accuracies().white() : game.accuracies().black();
            Double oppAcc = iAmWhite ? game.accuracies().black() : game.accuracies().white();
            entity.setPlayerAccuracy(myAcc != null ? BigDecimal.valueOf(myAcc) : null);
            entity.setOpponentAccuracy(oppAcc != null ? BigDecimal.valueOf(oppAcc) : null);
        }

        return entity;

    }

    private String extractGameId(String url) {
        // url like https://www.chess.com/game/live/168073095652  ->  168073095652
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private OffsetDateTime toOffsetDateTime(Long epochSeconds) {
        // Chess.com end_time is Unix seconds
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
    }

    private String extractOpeningName(String ecoUrl) {
        if (ecoUrl == null) return null;
        // .../openings/Closed-Sicilian-Defense-Traditional-Line  ->  Closed Sicilian Defense Traditional Line
        String slug = ecoUrl.substring(ecoUrl.lastIndexOf('/') + 1);
        return slug.replace('-', ' ');
    }

    private int countMoves(String pgn) {
        // crude move count: count occurrences of " <number>. " move markers is fiddly;
        // simplest reliable proxy — count full-move numbers like "1." "2." ... is error-prone,
        // so for now count the number of "." that follow a digit-run at move starts.
        // Simplest acceptable approach: count moves by splitting the movetext.
        if (pgn == null || pgn.isBlank()) return 0;
        int lastBrace = pgn.lastIndexOf(']');
        String movetext = lastBrace >= 0 ? pgn.substring(lastBrace + 1) : pgn;
        String[] tokens = movetext.trim().split("\\s+");
        int moves = 0;
        for (String t : tokens) {
            if (t.matches("\\d+\\.")) moves++;   // counts "1." "2." etc = full moves
        }
        return moves;
    }

    private String bucketResult(String rawResult) {
        return switch (rawResult) {
            case "win" -> "WIN";
            case "checkmated", "resigned", "timeout", "abandoned", "lose",
                 "kingofthehill", "threecheck", "bughousepartnerlose" -> "LOSS";
            case "stalemate", "agreed", "repetition", "insufficient",
                 "50move", "timevsinsufficient" -> "DRAW";
            default -> "UNKNOWN";
        };
    }

    private static final java.util.regex.Pattern ECO_PATTERN =
            java.util.regex.Pattern.compile("\\[ECO \"([^\"]+)\"\\]");

    private String extractEcoCode(String pgn) {
        if (pgn == null) return null;
        java.util.regex.Matcher matcher = ECO_PATTERN.matcher(pgn);
        return matcher.find() ? matcher.group(1) : null;
    }
}
