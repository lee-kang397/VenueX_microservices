package com.venuex.ApiGateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Define protected paths
        boolean isProtected = path.startsWith("/api/admin") ||
                              path.startsWith("/api/host") ||
                              path.startsWith("/api/user");

        // If NOT protected → skip JWT entirely
        if (!isProtected) {
            return chain.filter(exchange);
        }

        // Protected route → must have Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Integer userId = claims.get("id", Integer.class);

            // Attach user info to downstream request
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .header("X-User-Id", userId != null ? userId.toString() : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return Mono.error(new RuntimeException("Invalid JWT Token"));
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
