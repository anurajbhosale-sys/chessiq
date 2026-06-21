package io.chessiq.api.exception;

import io.chessiq.api.dto.response.ApiErrorResponse;
import io.chessiq.domain.exception.PlayerAlreadyExistsException;
import io.chessiq.infrastructure.chesscom.exception.PlayerNotFoundOnChessComException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handlePlayerAlreadyExists(PlayerAlreadyExistsException ex, HttpServletRequest request){
        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "PLAYER ALREADY EXISTS",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(PlayerNotFoundOnChessComException.class)
    public ResponseEntity<ApiErrorResponse> handlePlayerNotFoundOnChessCom(
            PlayerNotFoundOnChessComException ex, HttpServletRequest request) {

        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "PLAYER_NOT_FOUND_ON_CHESS_COM",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}