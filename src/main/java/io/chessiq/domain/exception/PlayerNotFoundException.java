package io.chessiq.domain.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String username) {
        super("Player not found: " + username);
    }
}
