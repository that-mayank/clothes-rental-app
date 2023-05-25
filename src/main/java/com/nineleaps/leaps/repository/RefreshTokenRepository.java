package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {
    RefreshToken findByEmail(String email);

}
