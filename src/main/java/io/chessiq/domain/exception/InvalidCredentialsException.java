package io.chessiq.domain.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials"); // deliberately vague — never says which part failed
    }
}