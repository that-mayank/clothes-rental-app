package com.nineleaps.leaps.service;

import com.nineleaps.leaps.model.AuthenticationToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {
    private final TokenRepository tokenRepository;

    @Autowired
    public AuthenticationService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void saveToken(AuthenticationToken authenticationToken) {
        tokenRepository.save(authenticationToken);
    }
    public AuthenticationToken getToken(User user) {
        return tokenRepository.findTokenByUser(user);
    }
}
