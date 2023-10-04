package com.nineleaps.leaps.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RefreshTokenTest {

    @Test
    void testCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreatedAt(createdAt);
        assertEquals(createdAt, refreshToken.getCreatedAt());
    }

    @Test
    void testEndingAt() {
        LocalDateTime endingAt = LocalDateTime.now().plusHours(2);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEndingAt(endingAt);
        assertEquals(endingAt, refreshToken.getEndingAt());
    }

    @Test
    void testEmail() {
        String email = "test@example.com";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail(email);
        assertEquals(email, refreshToken.getEmail());
    }
}
