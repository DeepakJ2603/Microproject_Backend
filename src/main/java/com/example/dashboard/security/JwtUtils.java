package com.example.dashboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ── Signing Key ───────────────────────────────────────────────────────────
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Generate Token ────────────────────────────────────────────────────────
    public String generateToken(String email, String role, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role",   role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Get Email from Token ──────────────────────────────────────────────────
    public String getEmailFromToken(String token) {
        return parseAllClaims(token).getSubject();
    }

    // ── Get Role from Token ───────────────────────────────────────────────────
    public String getRoleFromToken(String token) {
        return parseAllClaims(token).get("role", String.class);
    }

    // ── Get UserId from Token ─────────────────────────────────────────────────
    public Long getUserIdFromToken(String token) {
        return parseAllClaims(token).get("userId", Long.class);
    }

    // ── Get Expiration from Token ─────────────────────────────────────────────
    public Date getExpirationFromToken(String token) {
        return parseAllClaims(token).getExpiration();
    }

    // ── Check if Expired ──────────────────────────────────────────────────────
    public boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    // ── Validate Token ────────────────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (JwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token is empty or null: {}", e.getMessage());
            return false;
        }
    }

    // ── Parse Claims ──────────────────────────────────────────────────────────
    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}