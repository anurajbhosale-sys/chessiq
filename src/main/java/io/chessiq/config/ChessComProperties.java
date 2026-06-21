package io.chessiq.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "chessiq.chess-com")
public record ChessComProperties(

        @NotBlank String baseUrl,
        @NotBlank String userAgent

) {}