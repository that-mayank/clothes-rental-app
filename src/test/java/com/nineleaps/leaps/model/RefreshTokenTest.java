package com.nineleaps.leaps.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefreshTokenTest {

    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken();
    }

    @Test
    void getEmail() {
        refreshToken.setEmail("test@example.com");
        assertEquals("test@example.com", refreshToken.getEmail());
    }

    @Test
    void getToken() {
        refreshToken.setToken("validRefreshToken");
        assertEquals("validRefreshToken", refreshToken.getToken());
    }

    @Test
    void setEmail() {
        refreshToken.setEmail("new@example.com");
        assertEquals("new@example.com", refreshToken.getEmail());
    }

    @Test
    void setToken() {
        refreshToken.setToken("newRefreshToken");
        assertEquals("newRefreshToken", refreshToken.getToken());
    }
}
