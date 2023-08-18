package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken findByEmail(String email);
    void deleteByEmailAndToken(String email, String token);
}
