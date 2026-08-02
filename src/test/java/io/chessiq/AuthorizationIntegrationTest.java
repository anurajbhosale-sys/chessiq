package io.chessiq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationIntegrationTest extends IntegrationTest {

    @Autowired
    TestRestTemplate rest;   // real HTTP client aimed at the booted app

    // --- helpers ---

    private String signupAndLogin(String email, String password) {
        // signup (ignore result — may already exist within a run)
        rest.postForEntity("/api/auth/signup",
                Map.of("email", email, "password", password), String.class);
        // login -> pull the token out of the JSON body
        ResponseEntity<Map> login = rest.postForEntity("/api/auth/login",
                Map.of("email", email, "password", password), Map.class);
        return (String) login.getBody().get("token");
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        return h;
    }

    private ResponseEntity<String> post(String path, String token) {
        return rest.exchange(path, HttpMethod.POST,
                new HttpEntity<>(bearer(token)), String.class);
    }

    private ResponseEntity<String> get(String path, String token) {
        return rest.exchange(path, HttpMethod.GET,
                new HttpEntity<>(bearer(token)), String.class);
    }

    // --- the truth table ---

    @Test
    void noBadge_isUnauthorized() {
        ResponseEntity<String> r = rest.postForEntity(
                "/api/players/anyone/sync", null, String.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); // 401
    }

    @Test
    void fullOwnershipTruthTable() {
        // owner registers their player (stamps ownership)
        String owner = signupAndLogin("owner@test.com", "owner-pass-123");
        rest.exchange("/api/players", HttpMethod.POST,
                new HttpEntity<>(Map.of("chessComUsername", "ownerplayer"), bearer(owner)),
                String.class);

        // a second, unrelated user
        String attacker = signupAndLogin("attacker@test.com", "attacker-pass-123");

        // attacker -> owner's sync = 403
        assertThat(post("/api/players/ownerplayer/sync", attacker).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        // attacker -> ghost player = 404
        assertThat(post("/api/players/ghost-nobody/sync", attacker).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        // attacker -> owner's rebuild-stats = 403
        assertThat(post("/api/players/ownerplayer/rebuild-stats", attacker).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        // attacker -> owner's profile = 200 (lobby, authenticated-public)
        assertThat(get("/api/players/ownerplayer", attacker).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        // owner -> own sync = 202
        assertThat(post("/api/players/ownerplayer/sync", owner).getStatusCode())
                .isEqualTo(HttpStatus.ACCEPTED);
    }
}