package com.nineleaps.leaps.utils;

import com.nineleaps.leaps.repository.AccessTokenRepository;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class TokenCleanupJob {
    private final AccessTokenRepository accessTokenRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 * * * * *") // Schedule the job to run every minute
    public void cleanupExpiredAccessTokens() {
        Date now = new Date();
        // Find and update expired access tokens
        accessTokenRepository.findAllByIsExpiredFalseAndTokenExpiryBefore(now)
                .forEach(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                    accessTokenRepository.save(token);
                });
    }


    @Scheduled(cron = "0 * * * * *") // Schedule the job to run every minute
    public void cleanupExpiredRefreshTokens() {
        Date now = new Date();
        // Find and update expired refresh tokens
        refreshTokenRepository.findAllByIsExpiredIsFalseAndTokenExpiryBefore(now)
                .forEach(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

}


