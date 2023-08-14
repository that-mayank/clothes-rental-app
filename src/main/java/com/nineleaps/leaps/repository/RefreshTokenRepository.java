package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.tokens.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // Add any custom query methods if needed
    RefreshToken findByUser(User user);
    RefreshToken findByJwtTokenAndUser_Email(String jwtToken, String userEmail);
    RefreshToken findByJwtTokenAndUser_EmailAndUserDeviceDetail_UniqueDeviceId(String jwtToken, String email, String uniqueDeviceId);

    List<RefreshToken> findAllByIsExpiredIsFalseAndTokenExpiryBefore(Date date);

}

