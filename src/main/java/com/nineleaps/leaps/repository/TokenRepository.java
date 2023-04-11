package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.AuthenticationToken;
import com.nineleaps.leaps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<AuthenticationToken, Long> {
    AuthenticationToken findTokenByUser(User user);
    AuthenticationToken findTokenByToken(String token);
}
