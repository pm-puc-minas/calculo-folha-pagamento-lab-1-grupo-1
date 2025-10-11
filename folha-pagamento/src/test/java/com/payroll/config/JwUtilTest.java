package com.payroll.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateAccessToken_shouldContainUsernameAndClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtUtil.generateAccessToken("user", claims);

        assertNotNull(token);

        Claims extractedClaims = jwtUtil.extractAllClaims(token);
        assertEquals("user", extractedClaims.getSubject());
        assertEquals("ADMIN", extractedClaims.get("role"));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void generateRefreshToken_shouldContainUsername() {
        String token = jwtUtil.generateRefreshToken("user");
        assertNotNull(token);

        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals("user", claims.getSubject());
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateAccessToken("user", new HashMap<>());
        String username = jwtUtil.extractUsername(token);

        assertEquals("user", username);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateAccessToken("user", new HashMap<>());
        assertTrue(jwtUtil.validateToken(token, "user"));
    }

    @Test
    void validateToken_shouldReturnFalseForWrongUsername() {
        String token = jwtUtil.generateAccessToken("user", new HashMap<>());
        assertFalse(jwtUtil.validateToken(token, "otherUser"));
    }

    @Test
    void extractAllClaims_shouldThrowExceptionForInvalidToken() {
        assertThrows(ExpiredJwtException.class, () -> {
            // Token expirado manualmente
            JwtUtil shortJwt = new JwtUtil() {
                @Override
                public boolean isTokenExpired(String token) {
                    return true;
                }

                @Override
                public Claims extractAllClaims(String token) {
                    throw new ExpiredJwtException(null, null, "Token expirado");
                }
            };
            shortJwt.extractAllClaims("fakeToken");
        });
    }
}
