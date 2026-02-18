package com.example.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    // constructor-based injection
    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // Generate Token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // This "stamps" the role into the token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // Validate Token
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    // Extract email from token
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    // Extract the role string (e.g., "SUPER_USER", "ADMIN") from the token
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (claims != null) ? claims.get("role", String.class) : null;
    }

    // Extract Expiration Date
    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    // check if token is expired
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // If the token is trash, return null so the Filter can handle it gracefully
            return null;
        }
    }
}