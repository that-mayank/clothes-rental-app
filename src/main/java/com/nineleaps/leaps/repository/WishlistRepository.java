package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByUserIdOrderByCreateDateDesc(Long userId);

    Wishlist findByUserIdAndProductId(Long userId, Long productId);
}
