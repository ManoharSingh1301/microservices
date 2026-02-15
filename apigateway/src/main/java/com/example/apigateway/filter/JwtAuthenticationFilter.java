package com.example.apigateway.filter;

import com.example.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    // Public endpoints that don't require JWT authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 🔍 LOG: Shows this filter runs on EVERY request
        System.out.println("🔐 JWT Filter invoked for: " + request.getMethod() + " " + path);

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(path)) {
            System.out.println("✅ Public endpoint - skipping JWT validation");
            return chain.filter(exchange);
        }

        // Extract Authorization header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        
        if (authHeaders == null || authHeaders.isEmpty()) {
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = authHeaders.get(0);
        
        if (!authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // Validate JWT token
            if (!jwtUtil.validateToken(token)) {
                System.out.println("❌ Invalid or expired token");
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Extract user information from token
            Long userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            String name = jwtUtil.extractName(token);

            System.out.println("✅ JWT Valid - User: " + name + " (ID: " + userId + ", Role: " + role + ")");

            // Add user information as headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .header("X-User-Name", name)
                    .build();

            // Continue with modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            return onError(exchange, "JWT validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Check if the endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Handle authentication errors
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format("{\"error\": \"%s\", \"message\": \"%s\"}", 
                status.getReasonPhrase(), message);
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // High priority - execute before other filters
    }
}
