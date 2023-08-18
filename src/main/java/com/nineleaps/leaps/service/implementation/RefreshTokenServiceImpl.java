package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.RefreshTokenServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenServiceInterface {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String getRefreshToken(String email) {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email);
        return refreshToken.getToken();
    }
    @Override
    public void deleteRefreshTokenByEmailAndToken(String email, String token) {
        refreshTokenRepository.deleteByEmailAndToken(email,token);
    }
}
