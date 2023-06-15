package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String getRefreshToken(String email) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email);
        return refreshToken.getToken();
    }

}
