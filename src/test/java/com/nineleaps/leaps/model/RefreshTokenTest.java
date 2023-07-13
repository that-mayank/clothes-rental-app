package com.nineleaps.leaps.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void testRefreshTokenClass() {
        // Create a RefreshToken object
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail("test@example.com");
        refreshToken.setToken("123456789");

        // Verify the values using assertions
        assertThat(refreshToken.getEmail()).isEqualTo("test@example.com");
        assertThat(refreshToken.getToken()).isEqualTo("123456789");
    }
}
