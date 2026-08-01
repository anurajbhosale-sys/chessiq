package io.chessiq.domain.exception;

public class PlayerAccessDeniedException extends RuntimeException {
    public PlayerAccessDeniedException(String username) {
        super("You do not have access to player: " + username);
    }
}
