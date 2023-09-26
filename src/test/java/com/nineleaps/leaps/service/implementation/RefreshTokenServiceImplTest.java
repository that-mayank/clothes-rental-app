package com.nineleaps.leaps.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;

class RefreshTokenServiceImplTest {

    @Test
    void testGetRefreshToken() {
        // Mock necessary data
        String email = "test@example.com";
        String expectedToken = "test_token";

        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenServiceImpl refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(expectedToken);

        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        // Call the method to be tested
        String actualToken = refreshTokenService.getRefreshToken(email);

        // Verify the result
        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);

        // Verify that refreshTokenRepository.findByEmail() was called
        verify(refreshTokenRepository, times(1)).findByEmail(email);
    }

    @Test
    void testDeleteRefreshTokenByEmailAndToken() {
        // Mock necessary data
        String email = "test@example.com";
        String token = "test_token";

        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenServiceImpl refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository);

        // Call the method to be tested
        refreshTokenService.deleteRefreshTokenByEmailAndToken(email, token);

        // Verify that refreshTokenRepository.deleteByEmailAndToken() was called
        verify(refreshTokenRepository, times(1)).deleteByEmailAndToken(email, token);
    }
}
