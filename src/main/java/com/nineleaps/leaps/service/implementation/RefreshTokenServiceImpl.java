package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service // Marks this class as a Spring service component
@AllArgsConstructor // Lombok's annotation to generate a constructor with all required fields
@Transactional // Marks this class as transactional for database operations
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {

    private final RefreshTokenRepository refreshTokenRepository;

    // Retrieve the refresh token associated with a specific email
    @Override
    public String getRefreshToken(String email) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email); // Find the refresh token by email
        return refreshToken.getToken(); // Return the token associated with the email
    }

    // Delete a specific refresh token by email and token value
    @Override
    public void deleteRefreshTokenByEmailAndToken(String email, String token) {
        refreshTokenRepository.deleteByEmailAndToken(email, token); // Delete the token using email and token values
    }
}
