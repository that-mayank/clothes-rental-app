package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner,String> {
    Banner findByBannerName(String bannername);
}
