package com.nineleaps.leaps.utils;

import com.nineleaps.leaps.model.tokens.AccessToken;
import com.nineleaps.leaps.model.tokens.RefreshToken;
import com.nineleaps.leaps.repository.AccessTokenRepository;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class TokenCleanupJob {
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = Logger.getLogger(TokenCleanupJob.class.getName());


    private EntityManager entityManager; // Inject the EntityManager


    private PlatformTransactionManager transactionManager; // Inject the TransactionManager

    @Scheduled(cron = "0 * * * * *") // Schedule the job to run every minute
    public void cleanupExpiredAccessTokens() {
        try {
            logger.log(Level.INFO, "Starting expired access tokens cleanup at: " + new Date());

            Date now = new Date();

            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            transactionTemplate.execute(status -> {
                List<AccessToken> expiredTokens = accessTokenRepository.findAllByIsExpiredFalseAndTokenExpiryBefore(now);
                for (AccessToken token : expiredTokens) {
                    String userId = token.getUser().getEmail();
                    String deviceUniqueId = token.getUserDeviceDetail().getUniqueDeviceId();

                    token.setExpired(true);
                    token.setRevoked(true);
                    accessTokenRepository.save(token);

                    logger.log(Level.INFO, "Expired access token cleanup completed for userId: " + userId +
                            " and deviceUniqueId: " + deviceUniqueId);
                }

                return null;
            });

            logger.log(Level.INFO, "Expired access tokens cleanup completed at: " + new Date());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred during expired access tokens cleanup: " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 * * * * *") // Schedule the job to run every minute
    public void cleanupExpiredRefreshTokens() {
        try {
            logger.log(Level.INFO, "Starting expired refresh tokens cleanup at: " + new Date());

            Date now = new Date();

            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            transactionTemplate.execute(status -> {
                List<RefreshToken> expiredTokens = refreshTokenRepository.findAllByIsExpiredIsFalseAndTokenExpiryBefore(now);
                for (RefreshToken token : expiredTokens) {
                    String userId = token.getUser().getEmail();
                    String deviceUniqueId = token.getUserDeviceDetail().getUniqueDeviceId();

                    token.setExpired(true);
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);

                    logger.log(Level.INFO, "Expired refresh token cleanup completed for userId: " + userId +
                            " and deviceUniqueId: " + deviceUniqueId);
                }

                return null;
            });

            logger.log(Level.INFO, "Expired refresh tokens cleanup completed at: " + new Date());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred during expired refresh tokens cleanup: " + e.getMessage(), e);
        }
    }

}
