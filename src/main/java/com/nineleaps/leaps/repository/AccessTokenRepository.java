package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.tokens.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    // Add any custom query methods if needed
    AccessToken findByUser(User user);
    AccessToken findByJwtTokenAndUser_Email(String jwtToken, String userEmail);

    List<AccessToken> findAllByIsExpiredFalseAndTokenExpiryBefore(Date date);
}



