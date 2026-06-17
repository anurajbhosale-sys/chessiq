package io.chessiq.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterPlayerRequest(

        @NotBlank(message = "Chess.com username is required")
        @Size(min = 1, max = 100, message = "Username must be between 1 and 100 characters")
        String chessComUsername

) {}