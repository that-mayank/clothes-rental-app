package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenServiceInterface{
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public String getrefreshToken(String email) {
         RefreshToken refresh_token = refreshTokenRepository.findByEmail(email);
        System.out.println(refresh_token.getRefresh_Token());
         return refresh_token.getRefresh_Token();
    }

    @Override
    public void getallrefreshtokens() {
        RefreshToken[] refreshTokens = refreshTokenRepository.findAll().toArray(new RefreshToken[0]);
        System.out.println(refreshTokens);
    }

    ;


}
