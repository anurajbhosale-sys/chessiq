package io.chessiq.infrastructure.chesscom.exception;

public class PlayerNotFoundOnChessComException extends RuntimeException{

    public PlayerNotFoundOnChessComException(String username){
        super("No Chess.com player found with username: " + username);
    }
}
