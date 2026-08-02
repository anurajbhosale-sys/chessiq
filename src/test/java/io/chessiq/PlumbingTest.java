package io.chessiq;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PlumbingTest extends IntegrationTest {

    @Test
    void contextLoadsAndContainerIsRunning() {
        // If this passes, the whole machine works: Docker started a Postgres,
        // Spring booted against it, Flyway migrated it, context is up.
        assertThat(postgres.isRunning()).isTrue();
    }
}