package io.chessiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ChessiqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessiqApplication.class, args);
	}

}
