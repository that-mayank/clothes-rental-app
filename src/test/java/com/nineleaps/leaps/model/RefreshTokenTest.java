package com.nineleaps.leaps.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
@Tag("unit_tests")
@DisplayName("RefreshToken Tests")
class RefreshTokenTest {

    @Test
    @DisplayName("Test Created At")
    void testCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreatedAt(createdAt);
        assertEquals(createdAt, refreshToken.getCreatedAt());
    }

    @Test
    @DisplayName("Test Ending At")
    void testEndingAt() {
        LocalDateTime endingAt = LocalDateTime.now().plusHours(2);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEndingAt(endingAt);
        assertEquals(endingAt, refreshToken.getEndingAt());
    }

    @Test
    @DisplayName("Test Email")
    void testEmail() {
        String email = "test@example.com";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail(email);
        assertEquals(email, refreshToken.getEmail());
    }
}
