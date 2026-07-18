package io.chessiq.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = Spring's base class guaranteeing exactly one
    // execution per request, even with internal forwards/dispatches.

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token, or not Bearer-shaped? NOT our problem — pass through.
        // The filter never rejects; it only vouches. Deciding whether
        // anonymous is acceptable for this path is SecurityConfig's job.
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // strip "Bearer "

            jwtService.extractUserId(token).ifPresent(userId -> {
                // Valid token -> tell Spring Security "this request is user X".
                // principal = the userId; credentials = null (token already
                // proved identity); authorities = empty (no roles yet).
                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // ^ SecurityContextHolder = per-request storage that
                //   anyRequest().authenticated() consults downstream.
            });
            // Invalid token -> Optional.empty() -> we set nothing -> the request
            // proceeds as anonymous and dies at the gate with 401/403 if the
            // path requires auth. All failure modes collapse into "anonymous."
        }

        filterChain.doFilter(request, response); // ALWAYS continue the chain
    }
}