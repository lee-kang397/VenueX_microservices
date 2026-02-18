package com.example.auth_service.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter implements Filter{

    private final JwtUtil jwtUtil;

    //List of URLs that don't need a token
    private final List<String> whiteList = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/events",
            "/api/venues");

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return; // STOP HERE! Do not call chain.doFilter()
        }

        String path = httpRequest.getRequestURI();
        // Whitelist
        if (whiteList.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String role = jwtUtil.extractRole(token);
                String email = jwtUtil.extractEmail(token);
                httpRequest.setAttribute("userEmail", email);
                httpRequest.setAttribute("userRole", role);

                // SUPER_USER Bypass
                if (role.equals("SUPER_USER")) {
                    chain.doFilter(request, response);
                    return; // Stop the filter here
                }

                // Admin Routes
                if (path.startsWith("/api/admin") && !role.equals("ADMIN")) {
                    httpResponse.setStatus(403);
                    httpResponse.getWriter().write("Access Denied: Admin role required");
                    return;
                }

                // Host Routes
                if (path.startsWith("/api/host") && !(role.equals("HOST") || role.equals("ADMIN"))) {
                    httpResponse.setStatus(403);
                    httpResponse.getWriter().write("Access Denied: Host or Admin role required");
                    return;
                }

                // Standard User Routes
                if (path.startsWith("/api/user") && role.equals("GUEST")) {
                    httpResponse.setStatus(403);
                    httpResponse.getWriter().write("Access Denied: Please register an account");
                    return;
                }

                // Success!

                chain.doFilter(request, response);
            } else {
                // Token was present but failed validation (expired/tampered)
                httpResponse.setStatus(401);
                httpResponse.getWriter().write("Unauthorized: Invalid or expired token");
            }
            return; // Stop here so we don't hit the 401 at the bottom
        }

        //If we reach this point, it means no "Bearer" header was present at all
        httpResponse.setStatus(401);
        httpResponse.getWriter().write("Unauthorized: Missing token");
    }

}
