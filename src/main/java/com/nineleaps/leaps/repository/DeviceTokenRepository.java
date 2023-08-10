package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserId(Long userId);
    Optional<DeviceToken> findByUserEmail(String email);
}

