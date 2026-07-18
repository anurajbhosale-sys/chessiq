package io.chessiq.config;

import io.chessiq.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Safe for a header-based API: CSRF only threatens cookie auth,
                // where browsers attach credentials automatically. Nothing
                // auto-attaches an Authorization header.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // No HTTP sessions, no JSESSIONID cookies — the token is the
                // only identity carrier. "Stateless" as config.
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    // Runs ONLY when an unidentified visitor hits a protected door.
                    // We answer: 401, plus the standard hint about how to authenticate.
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "Bearer");
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                })
        )





                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll()
                                // Bootstrap carve-out, permanent: you can't need a
                                // token to obtain a token.
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                        // Deny-by-default: everything not listed above,
                        // including all /api/players/**, now requires a token.
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        // Our checkpoint runs before authorization consults the
        // security context — identity attached first, decision second.
        return http.build();
    }
}