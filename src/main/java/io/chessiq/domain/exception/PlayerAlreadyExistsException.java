package io.chessiq.domain.exception;

public class PlayerAlreadyExistsException extends RuntimeException{

    public  PlayerAlreadyExistsException(String username){
        super("Player already exists: "+username );
    }
}
