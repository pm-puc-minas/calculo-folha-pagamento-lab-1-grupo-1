package com.payroll.config;
/*
 * Utilitário para criação e validação de tokens JWT.
 * Fornece geração de access/refresh tokens, extração de claims
 * e verificação de expiração e integridade do token.
 */

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "l7v5ffmMzaaGAXsvD3gekpNKYEEsc9GuMWTPxoTZ9VmtE4ylWr39u9OR2YFXTwHv0Ejh3Gld6Aue1foHDaK1kJZnylXmeNQYQNhY2GxAR0AmpAA0ANMhxynMveXrPoMDy5pnBfdBQacnc6aKIz2u6MA8HqlXDiIjz4pKIlN47sMCZuyugn2vP6qf2ooMoZv0uCU84fDsKw1SLYEWHq6k4QHBWqnZk7iTomD9Rj8CvuJjsxeoHc9QPYdjQfSboU3l";
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15 minutos
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7 dias

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Gera JWT com claims personalizadas
    public String generateAccessToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}

